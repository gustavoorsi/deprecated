package com.referaice.api.resources.assemblers;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;

import com.referaice.api.endpoints.ReferringRestEndpoint;
import com.referaice.api.resources.PersonResource;
import com.referaice.model.entitties.Person;

@Component
public class PersonResourceAssembler implements ResourceAssembler<Person, PersonResource> {

	@Override
	public PersonResource toResource(Person content) {

		PersonResource resource = new PersonResource(content);

		// add link to itself ( rel = self )
		Link selfLink = linkTo(methodOn(ReferringRestEndpoint.class).getReferring(content.getContactInfo().getEmail())).withSelfRel();
		resource.add(selfLink);

		return resource;
	}

}
