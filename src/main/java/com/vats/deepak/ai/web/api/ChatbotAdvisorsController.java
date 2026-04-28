package com.vats.deepak.ai.web.api;


import com.vats.deepak.ai.beans.ChatbotRequest;
import com.vats.deepak.ai.beans.ChatbotResponse;
import com.vats.deepak.ai.web.service.ChatbotMemoryService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ChatbotAdvisorsController {
    private final ChatClient inMemoryChatClient;
    private final ChatMemory chatMemory;

    private final String systemMessage = "You are my personal assistant";

    public ChatbotAdvisorsController(OpenAiChatModel openAiChatModel, ChatMemory chatMemory, ChatMemory chatMemory1) {
        this.chatMemory = chatMemory;
        //Step-1: prepare ChatMemory advisor
        var messageChatMemoryAdvisor = MessageChatMemoryAdvisor //injects conversation history in prompt
                .builder(chatMemory)
                .build();
        //Step-2: create ChatClient with aadavisor
        this.inMemoryChatClient = ChatClient
                .builder(openAiChatModel)
                .defaultAdvisors(messageChatMemoryAdvisor) //created ChatClient with Advisors
                .build();
    }

    @PostMapping(value = {"/api/advisor/chat"})
    public ChatbotResponse askQuestion(@RequestBody ChatbotRequest chatbotRequest) {
        String assistantAnswer =
                this.inMemoryChatClient
                        .prompt()
                        .system(systemMessage)
                        .user(chatbotRequest.question())
                        .call()
                        .chatResponse()
                        .getResult()
                        .getOutput()
                        .getText();

        return new ChatbotResponse(chatbotRequest.question(), assistantAnswer);
    }
}