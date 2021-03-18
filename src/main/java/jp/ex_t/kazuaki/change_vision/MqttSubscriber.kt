/*
 * MqttSubscriber.kt - pair-modeling-prototype
 * Copyright © 2021 HyodaKazuaki.
 *
 * Released under the MIT License.
 * see https://opensource.org/licenses/MIT
 */

package jp.ex_t.kazuaki.change_vision

import com.change_vision.jude.api.inf.AstahAPI
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

class MqttSubscriber(private val brokerAddress: String, private val topic: String, private val clientId: String, private val reflectTransaction: ReflectTransaction): MqttCallback {
    private var broker: String = "tcp://$brokerAddress:1883"
    private val api = AstahAPI.getAstahAPI()
    private lateinit var mqttClient: MqttClient

    fun subscribe() {
        val qos = 2

        mqttClient = MqttClient(broker, clientId, MemoryPersistence())
        mqttClient.setCallback(this)
        val mqttConnectOptions = MqttConnectOptions()
        mqttConnectOptions.isCleanSession = false

        mqttClient.connect(mqttConnectOptions)
        println("Connected to broker $broker")

        mqttClient.subscribe(topic, qos)
    }

    fun close() {
        mqttClient.disconnect()
        mqttClient.close()
        println("Closed connection.")
    }

    override fun connectionLost(cause: Throwable) {
        println("Connection lost.")
        println(cause)
        throw cause
        // TODO: retry?
    }

    override fun messageArrived(topic: String, message: MqttMessage) {
        val receivedMessage = message.payload.toString(Charsets.UTF_8)
        println("Received: $receivedMessage ($topic)")
        val receivedMessageArray = receivedMessage.split("&&")
        val parentName = receivedMessageArray[0]
        val childName = receivedMessageArray[1]
        if (parentName == "" || childName == "") return
        reflectTransaction.add(parentName, childName)
    }

    override fun deliveryComplete(token: IMqttDeliveryToken) {
        TODO("Not yet implemented")
    }
}