/*
 * ReflectTransaction.kt - pair-modeling
 * Copyright © 2021 HyodaKazuaki.
 *
 * Released under the MIT License.
 * see https://opensource.org/licenses/MIT
 */

package jp.ex_t.kazuaki.change_vision.apply_transaction

import com.change_vision.jude.api.inf.AstahAPI
import com.change_vision.jude.api.inf.exception.BadTransactionException
import com.change_vision.jude.api.inf.project.ProjectAccessor
import com.change_vision.jude.api.inf.ui.IPluginActionDelegate.UnExpectedException
import jp.ex_t.kazuaki.change_vision.Logging
import jp.ex_t.kazuaki.change_vision.event_listener.ProjectChangedListener
import jp.ex_t.kazuaki.change_vision.logger
import jp.ex_t.kazuaki.change_vision.network.*
import javax.swing.SwingUtilities

class ReflectTransaction(entityLUT: EntityLUT, private val projectChangedListener: ProjectChangedListener) {
    private val api: AstahAPI = AstahAPI.getAstahAPI()
    private val projectAccessor: ProjectAccessor = api.projectAccessor
    private val classDiagramApplyTransaction = ClassDiagramApplyTransaction(entityLUT)
    private val mindmapDiagramApplyTransaction = MindmapDiagramApplyTransaction(entityLUT)
    private val stateMachineDiagramApplyTransaction = StateMachineDiagramApplyTransaction(entityLUT)

    @Throws(UnExpectedException::class)
    fun transact(transaction: Transaction) {
        SwingUtilities.invokeLater {
            val transactionManager = projectAccessor.transactionManager
            try {
                projectAccessor.removeProjectEventListener(projectChangedListener)
                logger.debug("Start transaction.")
                transactionManager.beginTransaction()
                classDiagramApplyTransaction.apply(
                    transaction.operations.filterIsInstance<ClassDiagramOperation>()
                )
                mindmapDiagramApplyTransaction.apply(
                    transaction.operations.filterIsInstance<MindmapDiagramOperation>()
                )
                stateMachineDiagramApplyTransaction.apply(
                    transaction.operations.filterIsInstance<StateMachineDiagramOperation>()
                )
                transactionManager.endTransaction()
                logger.debug("Finished transaction.")
                api.viewManager.diagramViewManager.select(emptyArray())
            } catch (e: BadTransactionException) {
                transactionManager.abortTransaction()
                logger.warn("Failed to transaction.", e)
                throw UnExpectedException()
            } finally {
                projectAccessor.addProjectEventListener(projectChangedListener)
            }
        }
    }

    companion object : Logging {
        private val logger = logger()
    }
}