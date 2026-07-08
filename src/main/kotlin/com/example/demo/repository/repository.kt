package com.example.demo.repository

interface repository {
//    fun chat(): String
    suspend fun chat(ques: String): String
}