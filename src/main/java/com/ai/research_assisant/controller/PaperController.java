package com.ai.research_assisant.controller;

import com.ai.research_assisant.entity.BookmarkRequest;
import com.ai.research_assisant.entity.Paper;
import com.ai.research_assisant.entity.User;
import com.ai.research_assisant.service.PapersService;
import com.ai.research_assisant.service.PapersService;
import com.ai.research_assisant.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/paper")
public class PaperController {

    @Autowired
    private PapersService paperService;

    @GetMapping("/{id}")
    public ResponseEntity<Paper> getPaperById(@PathVariable String id) {
        Paper paper = paperService.getPaperById(id);
        return ResponseEntity.ok(paper);
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadPaper(@RequestParam("file") MultipartFile file) {
        try {
            Paper paper = paperService.savePaper(file);
            return ResponseEntity.ok(paper);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload paper: " + e.getMessage());
        }
    }
    @PostMapping("/bookmark")
    public ResponseEntity<?> bookmark(@RequestBody BookmarkRequest request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            ObjectId paperId = new ObjectId(request.getPaperId());

            List<ObjectId> updatedBookmarks = paperService.toggleBookmark(paperId, username);

            return ResponseEntity.ok().body(updatedBookmarks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to update bookmark: " + e.getMessage());
        }
    }
    @PostMapping("/unbookmark")
    public ResponseEntity<?> unBookmark(@RequestBody BookmarkRequest request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            ObjectId paperId = new ObjectId(request.getPaperId());

            paperService.unbookmark(paperId, username);

            return ResponseEntity.ok().body("Unbookmarked!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to unbookmark: " + e.getMessage());
        }
    }







}
