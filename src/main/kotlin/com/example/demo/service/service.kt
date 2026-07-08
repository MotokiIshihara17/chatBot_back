package com.example.demo.service

interface Services {
    suspend fun chat(ques:String, prompt:String, img: String): String
}