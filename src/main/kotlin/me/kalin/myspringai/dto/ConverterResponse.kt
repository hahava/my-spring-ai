package me.kalin.myspringai.dto

data class ConverterResponse<T>(
    val answer: T
)

data class Answer(
    val answer: String,
    val reason: String
)
