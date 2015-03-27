package com.referaice.web.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.referaice.model.entitties.User;
import com.referaice.web.config.security.LoggedInUser;

@Controller
@RequestMapping(value = "/companies")
public class CompaniesController {

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView getCompanies(@LoggedInUser User loggedInUser) {

		Map<String, Object> model = new HashMap<String, Object>();

		return new ModelAndView("companies/companies", model);

	}

}
