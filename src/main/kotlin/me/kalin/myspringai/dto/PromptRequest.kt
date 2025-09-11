package me.kalin.myspringai.dto

import me.kalin.myspringai.code.PromptType

data class PromptRequest(
    val chatType: PromptType,
    val question: String
)
