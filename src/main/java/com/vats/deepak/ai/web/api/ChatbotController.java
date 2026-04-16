package com.vats.deepak.ai.web.api;

import com.vats.deepak.ai.beans.ChatbotRequest;
import com.vats.deepak.ai.beans.ChatbotResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatbotController {

    private final ChatClient chatClient;

    public ChatbotController(ChatClient.Builder chatBuilder) {
        this.chatClient = chatBuilder.build();
    }

    @PostMapping({"/api/chat"})
    public ChatbotResponse getQuestion(@RequestBody ChatbotRequest request) {
        //ChatOptions chatOptions = ChatOptions.builder().model("gpt-5.4-nano").build();
        //Prompt prompt = new Prompt(request.question(), chatOptions);

        //fluent api used (like builder pattern)
        String answer = chatClient
                //.prompt(prompt)
                .prompt(request.question())
                .call()
                .content();
        return new ChatbotResponse(request.question(), answer);
    }
}
