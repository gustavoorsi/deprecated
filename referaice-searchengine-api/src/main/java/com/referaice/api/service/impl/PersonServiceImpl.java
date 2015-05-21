package com.referaice.api.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.referaice.api.service.PersonService;
import com.referaice.model.entitties.Person;
import com.referaice.model.entitties.exceptions.PersonAlreadyExistException;
import com.referaice.model.entitties.exceptions.PersonNotFoundException;
import com.referaice.model.repository.mongo.PersonRepository;

@Service
@Transactional
public class PersonServiceImpl implements PersonService<Person> {

	private final PersonRepository personRepository;
	private final MongoOperations mongoOperations;
	

	@Autowired
	public PersonServiceImpl(final PersonRepository personRepository, final MongoOperations mongoOperations) {
		this.personRepository = personRepository;
		this.mongoOperations = mongoOperations;
	}

	@Override
	public Page<Person> findAll(Pageable pageable) {
		return this.personRepository.findAll(pageable);
	}

	@Override
	public Person findById(String id) {
		return this.personRepository.findById(id).orElseThrow(() -> new PersonNotFoundException());
	}

	@Override
	public Person findByEmail(String email) {
		return this.personRepository.findByContactInfoEmail(email).orElseThrow(() -> new PersonNotFoundException(email));
	}

	@Override
	public Person addPerson(Person input) throws PersonAlreadyExistException {
		Person person;
		try{
			person = this.personRepository.save(input);
		} catch( DuplicateKeyException ex ){
			throw new PersonAlreadyExistException(ex);
		}
		
		return person;
	}

	@Override
	public Person updatePerson(Person input) {
		
		
		return null;
		
//		this.mongoOperations.findAndModify(Query.query( Criteria.where("contactInfo.email").is( input.getContactInfo().getEmail() ) ), Update.update(key, value), entityClass)
//		
//		
//		Person persisted = this.personRepository.findByContactInfoEmail( input.getContactInfo().getEmail() ).orElseThrow( () -> new PersonNotFoundException() );
//		
//		
//		
//		input.setId( persisted.getId() );
//		
//		input = this.personRepository.find
//		
//		return input;
	}
	
	
	
	

}
