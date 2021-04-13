/*
 * MindmapDiagramEventListener.kt - pair-modeling-prototype
 * Copyright © 2021 HyodaKazuaki.
 *
 * Released under the MIT License.
 * see https://opensource.org/licenses/MIT
 */

package jp.ex_t.kazuaki.change_vision.event_listener

import com.change_vision.jude.api.inf.model.IMindMapDiagram
import com.change_vision.jude.api.inf.model.INamedElement
import com.change_vision.jude.api.inf.presentation.ILinkPresentation
import com.change_vision.jude.api.inf.presentation.INodePresentation
import com.change_vision.jude.api.inf.project.ProjectEditUnit
import jp.ex_t.kazuaki.change_vision.Logging
import jp.ex_t.kazuaki.change_vision.logger
import jp.ex_t.kazuaki.change_vision.network.*
import kotlinx.serialization.ExperimentalSerializationApi

class MindmapDiagramEventListener(private val entityLUT: EntityLUT, private val mqttPublisher: MqttPublisher) :
    IEventListener {
    @ExperimentalSerializationApi
    override fun process(projectEditUnit: List<ProjectEditUnit>) {
        logger.debug("Start process")
        val removeProjectEditUnit = projectEditUnit.filter { it.operation == Operation.REMOVE.ordinal }
        val removeOperations = removeProjectEditUnit.mapNotNull { editUnit ->
            Operation.values()[editUnit.operation].let { op -> logger.debug("Op: $op -> ") }
            when (val entity = editUnit.entity) {
                is INodePresentation -> deleteTopic(entity)

                else -> {
                    logger.debug("$entity(Unknown)")
                    null
                }
            }
        }
        if (removeOperations.isNotEmpty()) {
            val removeTransaction = Transaction(removeOperations)
            ProjectChangedListener.encodeAndPublish(removeTransaction, mqttPublisher)
            return
        }

        val addProjectEditUnit = projectEditUnit.filter { it.operation == Operation.ADD.ordinal }
        val createOperations = mutableListOf<MindmapDiagramOperation>()
        for (it in addProjectEditUnit) {
            val operation = Operation.values()[it.operation]
            logger.debug("Op: $operation -> ")
            when (val entity = it.entity) {
                is IMindMapDiagram -> {
                    createOperations.add(createMindMapDiagram(entity))
                    break
                }
                is INodePresentation -> {
                    when (entity.diagram) {
                        is IMindMapDiagram -> {
                            when (entity.parent) {
                                null -> {
                                    createOperations.add(createFloatingTopic(entity))
                                }
                                else -> {
                                    createOperations.add(createTopic(entity) ?: return)
                                }
                            }
                        }
                        else -> {
                            logger.debug("${entity.label}(Unknown)")
                        }
                    }
                }
                is ILinkPresentation -> {
                    val source = entity.source.model
                    val target = entity.target.model
                    logger.debug("$source(Unknown) - ${entity.label}(ILinkPresentation) - $target(Unknown)")
                }
                else -> {
                    logger.debug("$entity(Unknown)")
                }
            }
        }
        if (createOperations.isNotEmpty()) {
            val createTransaction = Transaction(createOperations)
            ProjectChangedListener.encodeAndPublish(createTransaction, mqttPublisher)
            return
        }

        val modifyProjectEditUnit = projectEditUnit.filter { it.operation == Operation.MODIFY.ordinal }
        val modifyOperations = mutableListOf<MindmapDiagramOperation>()
        for (it in modifyProjectEditUnit) {
            val operation = Operation.values()[it.operation]
            logger.debug("Op: $operation -> ")
            when (val entity = it.entity) {
                is INodePresentation -> {
                    modifyOperations.add(resizeTopic(entity) ?: continue)
                }
                else -> {
                    logger.debug("$entity(Unknown)")
                }
            }
        }
        if (modifyOperations.isNotEmpty()) {
            val modifyTransaction = Transaction(modifyOperations)
            ProjectChangedListener.encodeAndPublish(modifyTransaction, mqttPublisher)
        }
    }

    private fun createMindMapDiagram(entity: IMindMapDiagram): CreateMindmapDiagram {
        val owner = entity.owner as INamedElement
        val rootTopic = entity.root
        logger.debug("${entity.name}(IMindMapDiagram)")
        entityLUT.entries.add(Entry(rootTopic.id, rootTopic.id))
        return CreateMindmapDiagram(entity.name, owner.name, rootTopic.id)
    }

    private fun createFloatingTopic(entity: INodePresentation): CreateFloatingTopic {
        val location = Pair(entity.location.x, entity.location.y)
        val size = Pair(entity.width, entity.height)
        entityLUT.entries.add(Entry(entity.id, entity.id))
        logger.debug("${entity.label}(INodePresentation, FloatingTopic)")
        return CreateFloatingTopic(entity.label, location, size, entity.diagram.name, entity.id)
    }

    private fun createTopic(entity: INodePresentation): CreateTopic? {
        entityLUT.entries.add(Entry(entity.id, entity.id))
        val parentEntry = entityLUT.entries.find { it.mine == entity.parent.id }
        if (parentEntry == null) {
            logger.debug("${entity.parent.id} not found on LUT.")
            return null
        }
        logger.debug("${entity.parent.label}(INodePresentation) - ${entity.label}(INodePresentation)")
        return CreateTopic(parentEntry.common, entity.label, entity.diagram.name, entity.id)
    }

    private fun resizeTopic(entity: INodePresentation): ResizeTopic? {
        return when (val diagram = entity.diagram) {
            is IMindMapDiagram -> {
                val location = Pair(entity.location.x, entity.location.y)
                val size = Pair(entity.width, entity.height)
                logger.debug(
                    "${entity.label}(INodePresentation)::Topic(${
                        Pair(
                            entity.width,
                            entity.height
                        )
                    } at ${entity.location}) @MindmapDiagram${diagram.name}"
                )
                val entry = entityLUT.entries.find { it.mine == entity.id } ?: run {
                    logger.debug("${entity.id} not found on LUT.")
                    return null
                }
                // Root topic and floating topic have no parent, so parent entry is empty. (It means parent id is also empty.)
                val parentEntry =
                    if (entity.parent == null) Entry("", "") else entityLUT.entries.find { it.mine == entity.parent.id }
                        ?: run {
                            logger.debug("${entity.parent.id} not found on LUT.")
                            return null
                        }
                ResizeTopic(entity.label, location, size, parentEntry.common, entry.common)
            }
            else -> {
                logger.debug("${entity.label}(INodePresentation) @UnknownDiagram")
                null
            }
        }
    }

    private fun deleteTopic(entity: INodePresentation): DeleteTopic? {
        return run {
            val lut = entityLUT.entries.find { it.mine == entity.id }
            if (lut == null) {
                logger.debug("${entity.id} not found on LUT.")
                null
            } else {
                entityLUT.entries.remove(lut)
                logger.debug("${entity}(INodePresentation) @MindmapDiagram")
                DeleteTopic(lut.common)
            }
        }
    }

    companion object : Logging {
        private val logger = logger()
    }
}