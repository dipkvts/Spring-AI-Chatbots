package com.vats.deepak.ai.web.service;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChatbotMemoryService {

    //Holds chat history
    ConcurrentHashMap<String, List<Message>> chatHistory = new ConcurrentHashMap<>();

    public boolean isExistedSession(String sessionId) {
        return chatHistory.containsKey(sessionId);
    }

    public List<Message> getChatHistory(String sessionId) {
        return chatHistory.getOrDefault(sessionId, new ArrayList<>());
    }

    public List<Message> addChatHistory(String sessionId, String question, String answer){
        List<Message> previousChats =  getChatHistory(sessionId);
        var newMessages = new ArrayList<>(previousChats);
        newMessages.add(new UserMessage(question));
        newMessages.add(new AssistantMessage(answer));
        chatHistory.put(sessionId, newMessages);
        return  newMessages;
    }

    //system msg is set only once for entire session
    public void addSystemMessage(String sessionId, String systemMessage){
        List<Message> messageList = new ArrayList<>();
        messageList.add(new SystemMessage(systemMessage));
        chatHistory.put(sessionId, messageList);
    }

}