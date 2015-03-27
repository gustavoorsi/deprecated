package com.referaice.model.repository.mongo;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.referaice.model.entitties.User;

public interface UserRepository extends MongoRepository<User, String>{
	
	Optional<User> findByEmail(String email);

}
