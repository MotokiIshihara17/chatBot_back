package com.example.demo.repository

import aws.sdk.kotlin.services.bedrockruntime.BedrockRuntimeClient
import aws.sdk.kotlin.services.bedrockruntime.model.ContentBlock
import aws.sdk.kotlin.services.bedrockruntime.model.ConversationRole
import aws.sdk.kotlin.services.bedrockruntime.model.ConverseRequest
import aws.sdk.kotlin.services.bedrockruntime.model.Message
import org.springframework.stereotype.Repository

@Repository
abstract class RepositoryClass(): repository {
    override suspend fun chat(ques: String): String {
        BedrockRuntimeClient { region = "us-east-1" }.use { client ->

            // Specify the model ID. For the latest available models, see:
            // https://docs.aws.amazon.com/bedrock/latest/userguide/models-supported.html
            val modelId = "amazon.nova-lite-v1:0"
//		val modelId = "us.anthropic.claude-haiku-4-5-20251001-v1:0"

            // Create the message with the user's prompt
//            val prompt = "さっきの答えなんだっけ？"
            val prompt = ques
            val message = Message {
                role = ConversationRole.User
                content = listOf(ContentBlock.Text(prompt))
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