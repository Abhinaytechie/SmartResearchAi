package com.ai.research_assisant.repository;

import com.ai.research_assisant.entity.Paper;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PaperRepository  extends MongoRepository<Paper, ObjectId> {
    Optional<Paper> findByTitle(String title);
    Optional<Paper> findById(ObjectId Id);
}
