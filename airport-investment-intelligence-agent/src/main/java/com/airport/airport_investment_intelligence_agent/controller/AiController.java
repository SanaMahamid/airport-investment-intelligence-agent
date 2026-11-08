package com.airport.airport_investment_intelligence_agent.controller;

import com.airport.airport_investment_intelligence_agent.service.AiChatService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final AiChatService aiChatService;

    public AiController(AiChatService aiChatService) {
        this.aiChatService = aiChatService;
    }

    @GetMapping("/ask")
    public String ask(
            @RequestParam String question,
            @RequestParam(defaultValue = "default") String conversationId) {

        return aiChatService.ask(question, conversationId);
    }
}