package com.ai.research_assisant.service;

import com.ai.research_assisant.entity.ResearchReq;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class ResearchService {



    private final WebClient webClient;
    @Value("${gemini.api.url}")
    private String geminiApiUrl;
    @Value("${gemini.api.key}")
    private String geminiApiKey;

    public ResearchService(WebClient.Builder webClientBuilder){
        this.webClient=webClientBuilder.build();
    }

    public String summarize(ResearchReq req) {
        String prompt=buildsummarizePrompt(req);
        return processContent(req,prompt);
    }
    public String explain(ResearchReq req) {
        String prompt=buildExplainPrompt(req);
        return processContent(req,prompt);
    }
    public String generate(ResearchReq req) {
        String prompt=buildCodePrompt(req);
        return processContent(req,prompt);
    }

    private String buildCodePrompt(ResearchReq req) {
        StringBuilder prompt=new StringBuilder();
        prompt.append("Analyze the following paper or document. If it contains code snippets, extract and return them. If the code is incomplete, pseudocode, or missing, then generate a complete, efficient, and well-structured implementation based on the concepts and methods described in the text. Choose the most appropriate programming language based on the content or state your choice if not specified. Make sure the code is clean, optimized, and easy to understand. and only provide the code do not give any other info");
        prompt.append(req.getContent());
        return String.valueOf(prompt);
    }


    private String buildExplainPrompt(ResearchReq req) {
        StringBuilder prompt=new StringBuilder();

        prompt.append("You are a smart and structured research assistant. Given any research document, academic project, or thesis content, provide a detailed, well-organized explanation in proper Markdown format. Your output must be human-readable, clean, and categorized under the following sections:\n" +
                "\n" +
                "Title\n" +
                "Clearly state the full title of the research or project.\n" +
                "\n" +
                " Authors / Contributors\n" +
                "List all authors, students, or contributors with identifiers or roles (if provided).\n" +
                "\n" +
                " Abstract / Overview\n" +
                "Summarize the entire work in 4–6 lines.\n" +
                "\n" +
                "Clearly mention the goal, methods used, and impact.\n" +
                "\n" +
                " Problem Statement\n" +
                "Explain the issue being solved or researched.\n" +
                "\n" +
                "Highlight its importance and the challenges it presents.\n" +
                "\n" +
                " Proposed Solution\n" +
                "Describe the approach or model used to solve the problem.\n" +
                "\n" +
                "Mention major features and innovations.\n" +
                "\n" +
                "Methodologies / Technologies Used\n" +
                "Bullet list or paragraphs explaining the tools, frameworks, and techniques involved (e.g., NLP, ML models, preprocessing, classification, etc.).\n" +
                "\n" +
                "Workflow / Architecture (if applicable)\n" +
                "Describe the system pipeline or research workflow in sequential steps.\n" +
                "\n" +
                "Use bullet points or numbered lists.\n" +
                "\n" +
                " Results / Insights (if present)\n" +
                "Summarize the findings, metrics, or observations.\n" +
                "\n" +
                "Mention accuracy, rankings, or other evaluation outcomes if provided.\n" +
                "\n" +
                "Conclusion & Future Scope\n" +
                "List what the project achieved and how it benefits the intended users or field.\n" +
                "\n" +
                "Suggest possible future enhancements, integrations, or broader applications.\n" +
                "\n" +
                "Key Takeaways\n" +
                "List 4–6 bullet points summarizing the essence of the project.\n" +
                "\n" +
                "References (if included)\n" +
                "List all the references with proper format:\n" +
                "Title – Author(s) – Source/Link (if any)\n" +
                "\n" +
                "Output format: Must be in valid Markdown with proper headings (##), bullet points, and readable formatting. Do not use raw code blocks for content.\n" +
                "\n\n");

        prompt.append(req.getContent());
        return String.valueOf(prompt);
    }

    public String processContent(ResearchReq req,String prompt) {
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

    private String buildsummarizePrompt(ResearchReq req) {

        StringBuilder prompt=new StringBuilder();

        prompt.append("Provide a clean, meaningfull summary of the entire content\n");

        prompt.append(req.getContent());
        return String.valueOf(prompt);

    }


}
