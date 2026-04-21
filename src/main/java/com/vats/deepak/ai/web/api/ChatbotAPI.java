package com.vats.deepak.ai.web.api;

import com.vats.deepak.ai.beans.QuestionAnswerBean;
import com.vats.deepak.ai.beans.QuestionBean;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatbotAPI {

    private final ChatClient chatClient;

    ChatbotAPI(ChatClient.Builder chatBuilder) {
        this.chatClient = chatBuilder.build();
    }

    @PostMapping(value = {"/question-to-llm"})
    public ResponseEntity<QuestionAnswerBean> getAnswer(@RequestBody QuestionBean question) {
        String answer = this.chatClient
                .prompt(question.getQuestion())
                .call() //uses default model (gpt 4.0 mini), if not configured / specified
                .content(); //returns response, Actual AI provider api gets called at this moment
        QuestionAnswerBean response = QuestionAnswerBean.builder().question(question.getQuestion()).answer(answer).build();
        //.builder().setQuestion(-): Wrong
        return ResponseEntity.ok(response);
    }

}
