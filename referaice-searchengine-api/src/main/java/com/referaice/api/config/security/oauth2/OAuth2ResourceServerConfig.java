package com.referaice.api.config.security.oauth2;

import static com.referaice.consants.ReferaiceConstants.API.RESOURCE_ID;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

import com.referaice.consants.ReferaiceConstants;

@Configuration
@EnableResourceServer
public class OAuth2ResourceServerConfig extends ResourceServerConfigurerAdapter {

	
	
	@Override
	public void configure(HttpSecurity http) throws Exception {

		// @formatter:off
         http
         	 // any request that mathes the following patter will required security with oauth2... 
//	         .requestMatchers().antMatchers( 
//	        		 						ReferaiceConstants.API.REFERRING,
//	        		 						ReferaiceConstants.API.REFERRING + ReferaiceConstants.API.GET_REFERRING
//	        		 						)
         .requestMatchers().anyRequest()
		.and()
			.authorizeRequests().anyRequest().permitAll();
		
//	         .authorizeRequests()
//	         .anyRequest().access("#oauth2.hasScope('read')");
         // @formatter:on
	}

	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.resourceId(RESOURCE_ID);
	}

}
