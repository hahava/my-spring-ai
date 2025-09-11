package me.kalin.myspringai.service

import me.kalin.myspringai.code.ConverterType
import me.kalin.myspringai.dto.Answer
import me.kalin.myspringai.dto.ConverterRequest
import me.kalin.myspringai.dto.ConverterResponse
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.prompt.PromptTemplate
import org.springframework.ai.converter.BeanOutputConverter
import org.springframework.ai.converter.ListOutputConverter
import org.springframework.ai.converter.MapOutputConverter
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class ConverterService(
    private val chatClient: ChatClient
) {
    private val listOutputConverter = ListOutputConverter()
    private val beanOutputConverter = BeanOutputConverter(object : ParameterizedTypeReference<List<Answer>>() {})
    private val mapOutputConverter = MapOutputConverter()

    fun convertProvider(converterRequest: ConverterRequest): Flux<ConverterResponse<*>> {
        return when (converterRequest.type) {
            ConverterType.LIST -> convertToList(converterRequest.question)

            ConverterType.MAP -> convertToMap(converterRequest.question)

            ConverterType.BEAN -> convertToBean(converterRequest.question)
        }
    }

    private fun convertToMap(question: String): Flux<ConverterResponse<*>> {
        val promptTemplateBuilder = PromptTemplate.builder()
            .template("{question}에 대한 답변을 최소 5개의 목록으로 호출하세요 {format}")
            .build()

        val prompt = promptTemplateBuilder.create(
            mapOf("question" to question, "format" to mapOutputConverter.format)
        )

        return chatClient.prompt(prompt)
            .stream()
            .content()
            .reduce("") { acc, next -> acc + next }
            .flux()
            .map {
                ConverterResponse(answer = mapOutputConverter.convert(it))
            }
    }

    private fun convertToList(question: String): Flux<ConverterResponse<*>> {
        val promptTemplateBuilder = PromptTemplate.builder()
            .template("{question}에 대한 답변을 최소 5개의 목록으로 호출하세요 {format}")
            .build()

        val prompt = promptTemplateBuilder.create(
            mapOf("question" to question, "format" to listOutputConverter.format)
        )

        return chatClient.prompt(prompt)
            .stream()
            .content()
            .reduce("") { acc, next -> acc + next }
            .flux()
            .map {
                ConverterResponse(answer = listOutputConverter.convert(it))
            }
    }

    private fun convertToBean(question: String): Flux<ConverterResponse<*>> {
        val promptTemplateBuilder = PromptTemplate.builder()
            .template("{question}에 대한 답변을 최소 5개의 목록으로 호출하세요 {format}")
            .build()

        val prompt = promptTemplateBuilder.create(
            mapOf("question" to question, "format" to beanOutputConverter.format)
        )

        return chatClient.prompt(prompt)
            .stream()
            .content()
            .reduce("") { acc, next -> acc + next }
            .flux()
            .mapNotNull {
                ConverterResponse(answer = beanOutputConverter.convert(it))
            }
    }
}
