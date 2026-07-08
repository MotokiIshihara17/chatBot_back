package com.example.demo.service


import aws.sdk.kotlin.services.bedrockruntime.BedrockRuntimeClient
import aws.sdk.kotlin.services.bedrockruntime.BedrockRuntimeClient.Companion.invoke
import aws.sdk.kotlin.services.bedrockruntime.model.ContentBlock
import aws.sdk.kotlin.services.bedrockruntime.model.ConversationRole
import aws.sdk.kotlin.services.bedrockruntime.model.ConverseRequest
import aws.sdk.kotlin.services.bedrockruntime.model.ConverseRequest.Companion.invoke
import aws.sdk.kotlin.services.bedrockruntime.model.ImageBlock
import aws.sdk.kotlin.services.bedrockruntime.model.Message
import aws.sdk.kotlin.services.bedrockruntime.model.Message.Companion.invoke
import com.example.demo.repository.RepositoryClass
import org.springframework.stereotype.Service
import java.util.Base64
import aws.sdk.kotlin.services.bedrockruntime.model.ImageFormat
import aws.sdk.kotlin.services.bedrockruntime.model.ImageSource


@Service
//open class ServiceClass(private val repository: RepositoryClass) : Services{
//    override suspend fun chat(ques: String): String {
//        val chat = repository.chat(ques)
//        return chat
//    }
//}

open class ServiceClass() : Services{
    override suspend fun chat(ques: String,prompt: String, img: String): String {
            BedrockRuntimeClient { region = "us-east-1" }.use { client ->

                // Specify the model ID. For the latest available models, see:
                // https://docs.aws.amazon.com/bedrock/latest/userguide/models-supported.html
                val modelId = "amazon.nova-lite-v1:0"
//		val modelId = "us.anthropic.claude-haiku-4-5-20251001-v1:0"

                // Create the message with the user's prompt
//            val prompt = "さっきの答えなんだっけ？"

                val image:ByteArray = Base64.getMimeDecoder().decode(img)

                val imageBlock = ContentBlock.Image(ImageBlock {
                    source = ImageSource.Bytes(image)
                    format = ImageFormat.Jpeg
                })
                val promptText = ContentBlock.Text(prompt)
                val context = ContentBlock.Text(ques)
                println(prompt)
                val message = Message {
                    role = ConversationRole.User
                    content = listOf(imageBlock,promptText,context)
                }

                // Configure the request with optional model parameters
                val request = ConverseRequest {
                    this.modelId = modelId
                    messages = listOf(message)
                    inferenceConfig {
                        maxTokens = 500 // Maximum response length
                        temperature = 0.5F // Lower values: more focused output
                        // topP = 0.8F // Alternative to temperature
                    }
                }

                // Send the request and process the model's response
                runCatching {
                    val response = client.converse(request)
                    return response.output!!.asMessage().content.first().asText()
                }.getOrElse { error ->
                    error.message?.let { e -> System.err.println("ERROR: Can't invoke '$modelId'. Reason: $e") }
                    throw RuntimeException("Failed to generate text with model $modelId", error)
                }
            }
    }
}
