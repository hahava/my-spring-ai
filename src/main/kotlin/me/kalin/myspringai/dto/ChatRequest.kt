package me.kalin.myspringai.dto

import me.kalin.myspringai.code.ChatType

data class ChatRequest(
    val chatType: ChatType,
    val question: String
)
