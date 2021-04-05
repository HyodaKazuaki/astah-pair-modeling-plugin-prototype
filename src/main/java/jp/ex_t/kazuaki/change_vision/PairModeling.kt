/*
 * PairModeling.kt - pair-modeling-prototype
 * Copyright © 2021 HyodaKazuaki.
 *
 * Released under the MIT License.
 * see https://opensource.org/licenses/MIT
 */

package jp.ex_t.kazuaki.change_vision

import com.change_vision.jude.api.inf.AstahAPI
import jp.ex_t.kazuaki.change_vision.apply_transaction.TransactionReceiver
import jp.ex_t.kazuaki.change_vision.event_listener.ProjectChangedListener
import jp.ex_t.kazuaki.change_vision.network.MqttPublisher
import jp.ex_t.kazuaki.change_vision.network.MqttSubscriber
import jp.ex_t.kazuaki.change_vision.network.MqttSubscriberConfig

class PairModeling(topic: String, private val clientId: String, private val brokerAddress: String) {
    private val topicTransaction = "$topic/transaction"

    // TODO: もしプロジェクト全体が欲しいとなった場合はトピックを別で生やす
    // TODO: もしチャットが欲しいとなった場合はトピックを別で生やす
    private lateinit var mqttPublisher: MqttPublisher
    private lateinit var projectChangedListener: ProjectChangedListener
    private lateinit var mqttSubscriber: MqttSubscriber
    private lateinit var transactionReceiver: TransactionReceiver

    fun start() {
        val api = AstahAPI.getAstahAPI()
        val projectAccessor = api.projectAccessor

        logger.debug("Launching publisher...")
        val topicTransactionPublisher = "$topicTransaction/$clientId"
        mqttPublisher = MqttPublisher(brokerAddress, topicTransactionPublisher, clientId)
        projectChangedListener = ProjectChangedListener(mqttPublisher)
        projectAccessor.addProjectEventListener(projectChangedListener)
        logger.debug("Published: $brokerAddress:$topicTransaction ($clientId")
        logger.info("Launched publisher.")

        logger.debug("Launching subscriber...")
        val topicTransactionSubscriber = "$topicTransaction/#"
        transactionReceiver = TransactionReceiver(projectChangedListener)
        val mqttSubscriberConfig = MqttSubscriberConfig(topicTransactionSubscriber, 2, transactionReceiver)
        mqttSubscriber = MqttSubscriber(brokerAddress, listOf(mqttSubscriberConfig), clientId)
        mqttSubscriber.subscribe()
        logger.debug("Subscribed: $brokerAddress:$topicTransaction ($clientId")
        logger.info("Launched subscriber.")
    }

    fun end() {
        val api = AstahAPI.getAstahAPI()
        val projectAccessor = api.projectAccessor

        logger.debug("Stopping subscriber...")
        mqttSubscriber.close()
        logger.info("Stopped subscriber.")

        logger.debug("Stopping publisher...")
        projectAccessor.removeProjectEventListener(projectChangedListener)
        logger.info("Stopped publisher.")
    }

    companion object : Logging {
        private val logger = logger()
    }
}