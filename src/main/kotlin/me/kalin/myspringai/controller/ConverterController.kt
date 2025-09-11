package me.kalin.myspringai.controller

import me.kalin.myspringai.dto.ConverterRequest
import me.kalin.myspringai.dto.ConverterResponse
import me.kalin.myspringai.service.ConverterService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
class ConverterController(
    private val converterService: ConverterService
) {

    @PostMapping(
        "/convert",
        produces = [MediaType.APPLICATION_NDJSON_VALUE],
        consumes = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun convert(@RequestBody converterRequest: ConverterRequest): Flux<ConverterResponse<*>> {
        return converterService.convertProvider(converterRequest)
    }
}
