package me.kalin.myspringai.configuration

import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.model.ChatModel
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AIConfiguration {
    @Bean
    fun chatClient(chatModel: ChatModel): ChatClient =
        ChatClient.builder(chatModel)
            .defaultSystem("모든 답변은 한국어로 하며 최대한 친절하게 그리고 출처를 함께 전달할 것")
            .build()
}
