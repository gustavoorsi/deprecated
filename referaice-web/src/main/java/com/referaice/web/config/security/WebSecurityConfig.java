
package com.referaice.web.config.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;

@Configuration
@EnableWebMvcSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Order(WebSecurityConfig.ORDER_SECURITY_GENERAL_CONFIG)
public abstract class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	protected final static int ORDER_SECURITY_AUTHORIZATION_CONFIG = 1;
	protected final static int ORDER_SECURITY_AUTHENTICATION_CONFIG = 2;
	protected final static int ORDER_SECURITY_GENERAL_CONFIG = 3;

}
