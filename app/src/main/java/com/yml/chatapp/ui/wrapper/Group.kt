package com.yml.chatapp.ui.wrapper

import java.io.Serializable

data class Group(
    val groupName: String,
    val image: String,
    val participants: ArrayList<String>,
    val groupId: String = ""
):Serializable

data class Participants(
    val checkedList:ArrayList<String>
):Serializable
