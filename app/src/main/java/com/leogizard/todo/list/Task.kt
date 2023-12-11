package com.leogizard.todo.list

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class Task (
    @SerialName("id")
    val id : String,
    @SerialName("content")
    val title: String,
    @SerialName("description")
    val description: String = "") : java.io.Serializable