package com.referaice.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

//@formatter:off
/**
* 
* Here is some explanation about the annotations we are using:
* 
* 
* @SpringBootApplication: Bootstrap spring boot and tells Spring that this class will act as a configuration source. (There can be many such classes.)
* 
* @EnableWebMvc: Enables DispatcherServlet, mappings, @Controller annotated beans. We definitely need this as we are using MVC to expose REST endpoints.
* 
* @author Gustavo Orsi
*
*/
//@formatter:on
@SpringBootApplication
@ComponentScan(basePackages = { "com.referaice" })
@EnableWebMvc
public class ApplicationConfig {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(ApplicationConfig.class, args);
	}

}
