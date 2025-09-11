package me.kalin.myspringai.dto

import me.kalin.myspringai.code.ConverterType

data class ConverterRequest(
    val question: String,
    val type: ConverterType
)
