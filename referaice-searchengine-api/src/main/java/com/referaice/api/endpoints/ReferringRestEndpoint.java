package com.referaice.api.endpoints;

import static com.referaice.consants.ReferaiceConstants.API.GET_REFERRING;
import static com.referaice.consants.ReferaiceConstants.API.REFERRING;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.referaice.api.resources.PersonResource;
import com.referaice.api.resources.assemblers.PersonResourceAssembler;
import com.referaice.api.service.PersonService;
import com.referaice.model.entitties.Person;

@RestController
@RequestMapping(REFERRING)
public class ReferringRestEndpoint {

	// *************************************************************//
	// *********************** PROPERTIES **************************//
	// *************************************************************//

	@Autowired
	private PersonResourceAssembler personResourceAssembler;

	@Autowired
	private PersonService<Person> personService;

	// *************************************************************//
	// ********************* REST ENDPOINTS ************************//
	// *************************************************************//

	@RequestMapping(method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_ATOM_XML_VALUE })
	public HttpEntity<PagedResources<PersonResource>> getReferrings( //
			@PageableDefault(size = 10, page = 0) Pageable pageable, //
			PagedResourcesAssembler<Person> assembler//
	) {

		Page<Person> page = this.personService.findAll(pageable);

		return new ResponseEntity<PagedResources<PersonResource>>(assembler.toResource(page, personResourceAssembler), HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, value = GET_REFERRING, produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_ATOM_XML_VALUE })
	public HttpEntity<PersonResource> getReferring(//
			@PathVariable String email//
	) {

		Person person = this.personService.findByEmail(email);

		return new ResponseEntity<PersonResource>(this.personResourceAssembler.toResource(person), HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE })
	public HttpEntity<?> createReferrings( //
			@RequestBody Person input //
	) {

		HttpHeaders httpHeaders = new HttpHeaders();

		input = this.personService.addPerson(input);

		httpHeaders.setLocation(linkTo(methodOn(ReferringRestEndpoint.class, input.getId()).getReferring(input.getId())).toUri());

		return new ResponseEntity<>(httpHeaders, HttpStatus.CREATED);

	}

	@RequestMapping( value = "/{email}", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE })
	public HttpEntity<?> updateReferrings( //
			@RequestBody Person input //
	) {

		HttpHeaders httpHeaders = new HttpHeaders();

		input = this.personService.updatePerson(input);

		httpHeaders.setLocation(linkTo(methodOn(ReferringRestEndpoint.class, input.getId()).getReferring(input.getId())).toUri());

		return new ResponseEntity<>("The resource was updated ok.", httpHeaders, HttpStatus.NO_CONTENT);

	}

}
