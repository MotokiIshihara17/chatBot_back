package com.example.demo.controller

import com.example.demo.dataclass.chat
import com.example.demo.service.ServiceClass
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.CrossOrigin


@RestController
open class ChatController(private val service: ServiceClass){
    @CrossOrigin(origins = ["http://localhost:5173"])
    @PostMapping("/chat")
    suspend fun getAnswer(@RequestBody request: chat): String {
        println(request.ques)
        println(request.prompt)
        println(request.img)
        val getAnswer = service.chat(request.ques,request.prompt,request.img)
        return getAnswer
    }
}
