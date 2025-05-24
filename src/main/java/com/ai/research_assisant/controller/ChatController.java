package com.ai.research_assisant.controller;

import com.ai.research_assisant.entity.ChatMessage;
import com.ai.research_assisant.entity.ChatMsgReq;
import com.ai.research_assisant.service.ChatService;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@NoArgsConstructor
@RestController
@CrossOrigin(origins = "http://localhost:5173/")
@RequestMapping("/api/paper")
public class ChatController {
    @Autowired
    private ChatService chatService;
    @PostMapping("/{paperid}/chat")
    public ResponseEntity<ChatMessage> chat(@PathVariable ObjectId paperid, @RequestBody ChatMsgReq msgReq){
        try {
            System.out.println(paperid);
            String msg = msgReq.getMessage();
            String response = chatService.getResponse(paperid, msg);
            return ResponseEntity.ok(new ChatMessage("bot", response));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
   }
}
