package me.kalin.myspringai.controller

import me.kalin.myspringai.dto.ChatRequest
import me.kalin.myspringai.service.ChatService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
class ChatController(
    private val chatService: ChatService
) {
    @PostMapping(
        "/chat",
        produces = [MediaType.APPLICATION_NDJSON_VALUE],
        consumes = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getAnswer(@RequestBody chatRequest: ChatRequest): Flux<String> {
        return chatService.provider(chatRequest)
    }
}
