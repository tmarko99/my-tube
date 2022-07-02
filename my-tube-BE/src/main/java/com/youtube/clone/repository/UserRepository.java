package com.youtube.clone.repository;

import com.youtube.clone.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findBySub(String sub);
//    Optional<User> findById(String id);
}
