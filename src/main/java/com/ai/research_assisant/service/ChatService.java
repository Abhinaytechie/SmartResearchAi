package com.ai.research_assisant.service;

import com.ai.research_assisant.entity.Paper;
import com.ai.research_assisant.entity.ResearchReq;
import com.ai.research_assisant.repository.PaperRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.Optional;
@Service
public class ChatService {
    private final WebClient webClient;
    @Value("${gemini.api.url}")
    private String geminiApiUrl;
    @Value("${gemini.api.key}")
    private String geminiApiKey;

    public ChatService(WebClient.Builder webClientBuilder){
        this.webClient=webClientBuilder.build();
    }
    @Autowired
    private PaperRepository paperRepository;
    public String getResponse(ObjectId paperid, String msg) {
        Optional<Paper> paper=paperRepository.findById(paperid);
        if(!paper.isEmpty()){
            Paper ResearchPapaer=paper.get();
            String prompt=buildChatPrompt(ResearchPapaer,msg);
            return processContent(ResearchPapaer,msg,prompt);
        }
        return "Inavalid Request";
    }
    public String buildChatPrompt(Paper paper,String msg){
        StringBuilder prompt=new StringBuilder();
        prompt.append("Analyse the file:").append(paper).append("Now Answer to the").append(msg).append("clean and crisp");
        return String.valueOf(prompt);
    }
    public String processContent(Paper paper,String msg, String prompt) {
        Map<String,Object> reqBody= Map.of("contents",new Object[]{
                Map.of("parts", new Object[]{
                        Map.of("text",prompt)
                })
        });

        String response= webClient.post()
                .uri(geminiApiUrl+geminiApiKey)
                .header("Content-Type","application/json")
                .bodyValue(reqBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        return extractText(response);
    }

    private String extractText(String response) {
        try{
            ObjectMapper mapper=new ObjectMapper();
            JsonNode rootNode=mapper.readTree(response);
            return rootNode.path("candidates").get(0).path("content").path("parts")
                    .get(0).path("text").asText();
        }catch (Exception e){
            throw new RuntimeException();
        }

    }
}
