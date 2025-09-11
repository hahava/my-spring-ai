package me.kalin.myspringai.service

import me.kalin.myspringai.code.PromptType
import me.kalin.myspringai.dto.PromptRequest
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.messages.SystemMessage
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class PromptService(
    private val chatClient: ChatClient
) {
    fun provider(promptRequest: PromptRequest): Flux<String> {
        return when (promptRequest.chatType) {
            PromptType.FEW_SHOT -> fewShot(promptRequest.question)

            PromptType.ZERO_SHOT -> zeroShot(promptRequest.question)

            PromptType.ROLE_ASSIGNMENT -> roleAssignment(promptRequest.question)

            PromptType.STEP_BACK -> stepBack(promptRequest.question)

            PromptType.CHAIN_OF_THOUGHT -> chainOfThought(promptRequest.question)

            PromptType.SELF_CONSISTENCY -> selfConsistency(promptRequest.question)

            else -> throw IllegalArgumentException()
        }
    }

    private fun selfConsistency(question: String): Flux<String> {
        val systemMessage = SystemMessage.builder()
            .text(
                """
                다음 내용은  [IMPORTANT, NOT_IMPORTANT] 둘 중 하나로 분류해 주세요
                레이블만 반환하세요
            """.trimIndent()
            )
            .build()

        val prompt = Prompt.builder()
            .messages(systemMessage, UserMessage(question))
            .build()

        return Flux.merge(
            chatClient.prompt(prompt).stream().content(),
            chatClient.prompt(prompt).stream().content(),
            chatClient.prompt(prompt).stream().content(),
            chatClient.prompt(prompt).stream().content(),
            chatClient.prompt(prompt).stream().content()
        ).groupBy { it }                     // 값별 그룹핑 (0/1)
            .flatMap { g -> g.count().map { g.key() to it } } // (값, 개수)
            .sort(compareByDescending { it.second })          // 개수 많은 순 정렬
            .next()                                // 첫 번째(최빈값)
            .map { it.first }
            .flux()
    }

    private fun zeroShot(question: String): Flux<String> {
        val prompt = Prompt.builder()
            .messages(UserMessage(question))
            .build()

        return chatClient.prompt(prompt).stream().content()
    }

    private fun fewShot(question: String): Flux<String> {
        val message = """
            모든 답변은 반드시 json 으로 하며 응답은 아래와 같은 형식으로 
            
            {
              "schema": "http://json-schema.org/draft/2020-12/schema",
              "title": "User",
              "type": "object",
              "properties": {
                "answer": {
                  "type": "string",
                  "description": "상세한 답변"
                },
                "timestamp": {
                  "type": "integer",
                  "description": "답변이 작성된 시간을 의미"
                }
              },
              "required": ["answer", "timestamp"],
              "additionalRequired": false
            }
           
            
            예시. 1
            질문: 안녕?
            {
                "answer": "안녕 무엇을 도와드릴까요?",
                "timestamp": 123412321 
            }
            
            예시. 2
            질문: 대한민국의 수도는 ?
            {
                "answer": "대한민국의 수도는 서울입니다.",
                "timestamp": 123412321
            }
            
            질문: $question
        """.trimIndent()

        val prompt = Prompt.builder()
            .messages(UserMessage(message))
            .build()

        return chatClient.prompt(prompt).stream().content()
    }

    private fun stepBack(question: String): Flux<String> {
        val systemMessage = SystemMessage.builder()
            .text(
                """
                사용자의 질문을 처리하기 위해 StepBack 프롬프트 기법을 사용하려 합니다.
                사용자 질문을 단계별로 재구성해 주세요.
                맨 마지막 질문은 사용자 질문과 일치해야 합니다.
                단계별 질문을 항목으로 하는 josn배열로 출력해주세요
            """.trimIndent()
            ).build()

        val prompt = Prompt.builder()
            .messages(UserMessage(question), systemMessage)
            .build()

        return chatClient.prompt(prompt).stream().content()
    }

    private fun roleAssignment(question: String): Flux<String> {
        val systemMessage = SystemMessage.builder()
            .text(
                """
                당신은 여행가이드 입니다.
                미식을 좋아하며 남들에세 말하기 좋아합니다.
                방문하고 싶은 식당 3곳정도를 추천하고 사유를 함께 전달해주세요.
            """.trimIndent()
            )
            .build()

        val prompt = Prompt.builder()
            .messages(systemMessage, UserMessage(question))
            .build()

        return chatClient.prompt(prompt).stream().content()
    }

    private fun chainOfThought(question: String): Flux<String> {
        val systemMessage = SystemMessage.builder()
            .text(
                """
                한 걸음씩 생각해 봅시다.
                
                [예시]
                질문: 제 동생이 2살때 저는 그 나이의 두배였어요.
                지금 저는 40살인데, 제 동생을 몇살일까요? 한 걸음씩 생각해봅시다.
                
                답변: 제 동생일 2살일 때 저는 2 *2 =4 살이었어요.
                그때부터 2년 차이가 나며, 제가 나이가 더 많습니다.
                지금 저는 40살이니, 제 동생은 40 - 2 = 38 살이에요. 정답은 38살입니다.
            """.trimIndent()
            )
            .build()

        val prompt = Prompt.builder()
            .messages(UserMessage(question), systemMessage)
            .build()

        return chatClient.prompt(prompt).stream().content()
    }
}
