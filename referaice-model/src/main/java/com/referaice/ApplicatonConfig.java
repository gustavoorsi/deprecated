package com.referaice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.referaice.model.Company;
import com.referaice.model.repository.mongo.CompanyRepository;

@SpringBootApplication
public class ApplicatonConfig implements CommandLineRunner {

	@Autowired
	private CompanyRepository companyRepository;

	@Override
	public void run(String... arg0) throws Exception {

		this.companyRepository.deleteAll();

		this.companyRepository.save(new Company("Globant", "a bad company"));
		this.companyRepository.save(new Company("Google", "a good company"));

		// find and print all companies.
		this.companyRepository.findAll().forEach(c -> System.out.println(c));

		for (Company c : this.companyRepository.findAll()) {
			System.out.println(this.companyRepository.findOne(c.getId()));
		}

	}

	public static void main(String[] args) throws Exception {
		SpringApplication.run(ApplicatonConfig.class, args);
	}

}
