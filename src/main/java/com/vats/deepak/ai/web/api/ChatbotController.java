package com.vats.deepak.ai.web.api;

import com.vats.deepak.ai.beans.ChatbotRequest;
import com.vats.deepak.ai.beans.ChatbotResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
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

    public ChatbotController(ChatClient.Builder chatBuilder) {
        this.chatClient = chatBuilder.build();
    }

    @PostMapping({"/api/chat"})
    public ChatbotResponse getQuestion(@RequestBody ChatbotRequest request) {
        //ChatOptions can (instead) be provided through properties file also
//        ChatOptions chatOptions = ChatOptions.builder()
//                .maxTokens(100) //Max 100 tokens in response
//                .temperature(0.5)
//                .build();
//        Prompt prompt = new Prompt(request.question(), chatOptions);

        //fluent api used (like builder pattern)
//        String answer = chatClient
//                //.prompt(prompt)
//                .prompt(request.question())
//                .call() //uses default model (gpt 4.0 mini), if not configured / specified
//                .content(); //returns response, Actual AI provider api gets called at this moment

        //Preparing for Context memory
        List<Message> msgList = new ArrayList<>();
        msgList.add(new SystemMessage("You are my personal assistant."));
        msgList.add(new UserMessage(request.question()));
        msgList.add(new AssistantMessage("Welcome Mr. Deepak Vats. Nice to meet you."));
        msgList.add(new UserMessage("Do you remember me ?"));
        Prompt msgPrompt = new Prompt(msgList);

        String answer = chatClient
                //.prompt(request.question())
                //.prompt(prompt) //added context memory
                .prompt(msgPrompt)
                .call() //uses default model (gpt 4.0 mini), if not configured / specified
                .chatResponse() //Adding metadata by using ChatRssponse
                .getResult()
                .getOutput()
                .getText();
        return new ChatbotResponse(request.question(), answer);
    }
}
