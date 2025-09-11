package me.kalin.myspringai.controller

import me.kalin.myspringai.dto.PromptRequest
import me.kalin.myspringai.service.PromptService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
class PromptController(
    private val promptService: PromptService
) {
    @PostMapping(
        "/chat",
        produces = [MediaType.APPLICATION_NDJSON_VALUE],
        consumes = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getAnswer(@RequestBody promptRequest: PromptRequest): Flux<String> {
        return promptService.provider(promptRequest)
    }
}
