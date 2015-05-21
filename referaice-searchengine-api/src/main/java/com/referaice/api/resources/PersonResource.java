package com.referaice.api.resources;


import org.springframework.hateoas.Resource;

import com.referaice.model.entitties.Person;

public class PersonResource extends Resource<Person> {

	public PersonResource(Person content) {
		super(content);
	}

}
