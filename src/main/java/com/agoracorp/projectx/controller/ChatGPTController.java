package com.agoracorp.projectx.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatGPTController {
	
	private final ChatClient chatClient;
	
	public ChatGPTController(ChatClient.Builder builder) {
		ChatMemory chatMemory = MessageWindowChatMemory.builder()
				.chatMemoryRepository(new InMemoryChatMemoryRepository())
				.maxMessages(5)
				.build();
		
		MessageChatMemoryAdvisor advisor = MessageChatMemoryAdvisor.builder(chatMemory).build();
		
		this.chatClient = builder
				.defaultAdvisors(advisor)
				.build();
	}

	@GetMapping
	public ResponseEntity<String> getChatResponse(@RequestBody String input){
		String content = chatClient.prompt().user(input).call().content();
		return ResponseEntity.ok(content);
	}

}
