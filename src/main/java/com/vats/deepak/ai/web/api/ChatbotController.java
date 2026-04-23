package com.vats.deepak.ai.web.api;

import com.vats.deepak.ai.beans.ChatbotRequest;
import com.vats.deepak.ai.beans.ChatbotResponse;
import com.vats.deepak.ai.web.service.ChatbotMemoryService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ChatbotController {

    private final ChatClient chatClient;
    private final ChatbotMemoryService chatMemoryService;

    private final String systemMessage = "You are mypersonal assistant";

    public ChatbotController(ChatClient.Builder chatBuilder, ChatbotMemoryService chatMemoryService) {
        this.chatClient = chatBuilder.build();
        this.chatMemoryService = chatMemoryService;
    }

    @PostMapping(value = {"/api/chat"})
    public ChatbotResponse askQuestion(@RequestBody ChatbotRequest chatbotRequest) {

        String sessionId = chatbotRequest.sessionId();

        // step-1: check new session or not
        boolean isExistedSession = chatMemoryService.isExistedSession(sessionId);
        List<Message> previousChatHistory = new ArrayList<>();
        if (isExistedSession) {
            previousChatHistory = chatMemoryService.getChatHistory(sessionId);
        }else{
            chatMemoryService.addSystemMessage(sessionId, systemMessage);
        }
        var newChatMessage = new ArrayList<>(previousChatHistory);

        newChatMessage.add(new UserMessage(chatbotRequest.question()));

        Prompt prompt = new Prompt(newChatMessage);
        String assistantAnswer =
                this.chatClient
                        .prompt(prompt)
                        .call()
                        .chatResponse()
                        .getResult().
                        getOutput().
                        getText();

        chatMemoryService.addChatHistory(sessionId, chatbotRequest.question(), assistantAnswer);

        return new ChatbotResponse(chatbotRequest.question(), assistantAnswer);
    }

//    @PostMapping({"/api/chat"})
//    public ChatbotResponse getQuestion(@RequestBody ChatbotRequest request) {
////        //ChatOptions can (instead) be provided through properties file also
////        ChatOptions chatOptions = ChatOptions.builder()
////                .maxTokens(100) //Max 100 tokens in response
////                .temperature(0.5)
////                .build();
////        Prompt prompt = new Prompt(request.question(), chatOptions);
//
////        //fluent api used (like builder pattern)
////        String answer = chatClient
////                //.prompt(prompt)
////                .prompt(request.question())
////                .call() //uses default model (gpt 4.0 mini), if not configured / specified
////                .content(); //returns response, Actual AI provider api gets called at this moment
//
//        //Preparing for Context memory
//        List<Message> msgList = new ArrayList<>();
//        msgList.add(new SystemMessage("You are my personal assistant."));
//        msgList.add(new UserMessage(request.question()));
//        msgList.add(new AssistantMessage("Welcome Mr. Deepak Vats. Nice to meet you."));
//        msgList.add(new UserMessage("Do you remember me ?"));
//        Prompt msgPrompt = new Prompt(msgList);
//
//        String answer = chatClient
//                //.prompt(request.question())
//                //.prompt(prompt) //added context memory
//                .prompt(msgPrompt)
//                .call() //uses default model (gpt 4.0 mini), if not configured / specified
//                .chatResponse() //Adding metadata by using ChatRssponse
//                .getResult()
//                .getOutput()
//                .getText();
//        return new ChatbotResponse(request.question(), answer);
//    }
}
