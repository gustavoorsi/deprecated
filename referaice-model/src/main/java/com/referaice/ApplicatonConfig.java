package com.referaice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.referaice.model.repository.mongo.CompanyRepository;
import com.referaice.model.repository.mongo.UserRepository;

@SpringBootApplication
public class ApplicatonConfig implements CommandLineRunner {

	@Autowired
	private CompanyRepository companyRepository;
	
	@Autowired
	private UserRepository userRepository;

	public static void main(String[] args) throws Exception {
		SpringApplication.run(ApplicatonConfig.class, args);
	}

	@Override
	public void run(String... arg0) throws Exception {

//		this.companyRepository.deleteAll();
//		this.userRepository.deleteAll();

	}

}
