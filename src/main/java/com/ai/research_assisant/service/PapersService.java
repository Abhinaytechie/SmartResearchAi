package com.ai.research_assisant.service;

import com.ai.research_assisant.entity.Paper;
import com.ai.research_assisant.entity.User;
import com.ai.research_assisant.repository.PaperRepository;
import com.ai.research_assisant.repository.UserRepo;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class PapersService {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PaperRepository paperRepository;


    public Paper savePaper(MultipartFile file) throws IOException {
        String content = extractTextFromPdf(file);
        String title = extractTitle(content);
        String authorsStr = extractAuthors(content);
        List<String> authorsList = Arrays.stream(authorsStr.split(",| and "))
                .map(String::trim)
                .collect(Collectors.toList());
        String abstractText = extractAbstract(content);

        Paper paper = new Paper();
        paper.setTitle(title);
        paper.setAuthors(authorsList);
        paper.setAbstractText(abstractText);
        paper.setContent(content);
        paper.setUploadDate(LocalDateTime.now().toString());

        return paperRepository.save(paper);
    }

    private String extractTextFromPdf(MultipartFile file) throws IOException {
        byte[] fileBytes = file.getBytes();
        try {
            PDDocument document = Loader.loadPDF(fileBytes);
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            document.close();
            return text;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private String extractTitle(String content) {
        String[] lines = content.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (!line.isEmpty() && line.length() > 10 && !line.matches(".*[.,].*") && !line.equals(line.toUpperCase())) {
                return line;
            }
        }
        return "Untitled Paper";
    }


    private String extractAuthors(String content) {
        String[] lines = content.split("\n");
        for (String line : lines) {
            if (line.toLowerCase().contains("author") || line.matches(".*[A-Z][a-z]+\\s[A-Z][a-z]+.*")) {
                return line.trim();
            }
        }
        return "Unknown Authors";
    }


    private String extractAbstract(String content) {
        Pattern pattern = Pattern.compile("(?i)(abstract)([\\s\\S]*?)(?=(introduction|keywords|1\\.|I\\.|\\n\\n))");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(2).trim();
        }
        return "Abstract not found";
    }
    public List<ObjectId> toggleBookmark(ObjectId paperId, String username) {
        User user = userRepo.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        List<ObjectId> bookmarks = user.getBookmarkedPapersId();
        if (bookmarks.contains(paperId)) {
            bookmarks.remove(paperId);
        } else {
            bookmarks.add(paperId);
        }
        user.setBookmarkedPapersId(bookmarks);
        userRepo.save(user);

        return bookmarks;
    }
    public void unbookmark(ObjectId paperId, String username) {
        User user = userRepo.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        List<ObjectId> bookmarks = user.getBookmarkedPapersId();
        if (bookmarks.contains(paperId)) {
            bookmarks.remove(paperId);
            user.setBookmarkedPapersId(bookmarks);
            userRepo.save(user);
        }
    }


    public void bookmark(ObjectId paperId, String username) {
        User user = userRepo.findByUsername(username);
        if (user == null) throw new RuntimeException("User not found");

        boolean paperExists = paperRepository.existsById(paperId);
        if (!paperExists) throw new RuntimeException("Paper not found");

        if (!user.getBookmarkedPapersId().contains(paperId)) {
            user.getBookmarkedPapersId().add(paperId);
            userRepo.save(user);
        }
    }

    public List<Paper> getBookmarkedPapers(String username) {
        User user = userRepo.findByUsername(username);
        if (user == null) throw new RuntimeException("User not found");

        List<ObjectId> bookmarkIds = user.getBookmarkedPapersId();
        if (bookmarkIds.isEmpty()) return Collections.emptyList();

        return paperRepository.findAllById(bookmarkIds);
    }

}


