package com.ai.research_assisant.repository;

import com.ai.research_assisant.entity.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepo extends MongoRepository<User, ObjectId> {
    User findByUsername(String username);
    boolean existsByUsername(String username);
}
