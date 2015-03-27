package com.referaice.model.repository.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.referaice.model.Company;

public interface CompanyRepository extends MongoRepository<Company, String>{

}
