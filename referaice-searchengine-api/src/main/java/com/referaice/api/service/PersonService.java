package com.referaice.api.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.referaice.model.entitties.exceptions.PersonAlreadyExistException;

public interface PersonService<Person> {

	Page<Person> findAll(Pageable pageable);

	Person findById(final String id);
	
	Person findByEmail(final String email);
	
	Person addPerson(Person input) throws PersonAlreadyExistException;
	
	Person updatePerson(Person input);

}
