package com.vats.deepak.ai.web.api;


import com.vats.deepak.ai.beans.ChatbotRequest;
import com.vats.deepak.ai.beans.ChatbotResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.http.server.DelegatingServerHttpResponse;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        //Step-2: create ChatClient with adavisor
        this.inMemoryChatClient = ChatClient
                .builder(openAiChatModel)
                .defaultAdvisors(messageChatMemoryAdvisor) //created ChatClient with Advisors
                .build();
    }

    @PostMapping(value = {"/api/advisor/chat"})
    public ChatbotResponse askQuestion(@RequestBody ChatbotRequest chatbotRequest) {
        //No manual code written for chat history fetch pre-request
        var chatRequest = this.inMemoryChatClient
                .prompt()
                .system(systemMessage)
                .user(chatbotRequest.question())
                .advisors(advisror -> advisror.param(ChatMemory.CONVERSATION_ID, chatbotRequest.sessionId()));
                //Replaced default session id with programmer defined session id to have control over it
                //Now u need to pass sessionID parameter also during api call

        String assistantAnswer = chatRequest
                .call()
                .chatResponse()
                .getResult()
                .getOutput()
                .getText();
        //No manual code written for chat history update post-response

        return new ChatbotResponse(chatbotRequest.question(), assistantAnswer);
    }

    @DeleteMapping(value = {"/api/advisor/chat/{sessionId}"})
    public Map<String, Boolean> deleteConversation(@PathVariable String sessionId) {
        Map<String, Boolean> responseMap = new HashMap<>();
        chatMemory.clear(sessionId);
        List<Message> conversationList = chatMemory.get(sessionId);
        if(CollectionUtils.isEmpty(conversationList)) {
            responseMap.put("IsConversationDeleted", true);
        } else {
            responseMap.put("IsConversationDeleted", false);
        }

        return responseMap;
    }
}