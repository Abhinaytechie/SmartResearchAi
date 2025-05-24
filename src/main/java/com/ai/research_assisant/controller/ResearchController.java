package com.ai.research_assisant.controller;

import com.ai.research_assisant.entity.ResearchReq;
import com.ai.research_assisant.service.ResearchService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/paper")
@CrossOrigin(origins = "*")
public class ResearchController {

    private final ResearchService researchService;
    @PostMapping("/summarize")
    public ResponseEntity<String> summarize(@RequestBody ResearchReq req){

        String response=researchService.summarize(req);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/explain")
    public ResponseEntity<String> explain(@RequestBody ResearchReq req){

        String response=researchService.explain(req);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/generate-code")
    public ResponseEntity<String> genrateCode(@RequestBody ResearchReq req){

        String response=researchService.generate(req);
        return ResponseEntity.ok(response);
    }
}
