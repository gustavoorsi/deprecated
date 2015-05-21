package com.referaice.api.config.security.oauth2;

import static com.referaice.consants.ReferaiceConstants.API.RESOURCE_ID;
import static com.referaice.model.entitties.Role.ROLE_CLIENT;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;

/**
 * 
 * 
 * @author Gustavo Orsi
 *
 */
@Configuration
// The convenient annotation @EnableAuthorizationServer is used. The server is customized by extending the class
// AuthorizationServerConfigurerAdapter which provides empty method implementations for the interface AuthorizationServerConfigurer
// [1]
@EnableAuthorizationServer
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class Oauth2AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

	private enum GrantType {

		AUTHORIZATION_CODE("authorization_code"), CLIENT_CREDENTIALS("client_credentials");

		private String name;

		private GrantType(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return this.name;
		}
	}

	private enum SecurityScope {

		READ("read"), TRUST("trust");

		private String name;

		private SecurityScope(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return this.name;
		}
	}

	@Override
	// [2]
	// The configure method here setup the clients that can access the server. An in memory client detail service is used here for demo
	// purpose.
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		// @formatter:off
         clients.inMemory()
	         .withClient("client-with-registered-redirect")
		         .authorizedGrantTypes( GrantType.AUTHORIZATION_CODE.name)
		         .authorities( ROLE_CLIENT.getName() )
		         .scopes(SecurityScope.READ.name, SecurityScope.TRUST.name)
		         .resourceIds(RESOURCE_ID)
		         .redirectUris("http://anywhere?key=value")
		         .secret("secret123")
         .and()
	         .withClient("my-client-with-secret")
		         .authorizedGrantTypes(GrantType.CLIENT_CREDENTIALS.name)
		         .authorities(ROLE_CLIENT.getName())
		         .scopes(SecurityScope.TRUST.name)
		         .resourceIds(RESOURCE_ID)
		         .secret("secret");
         // @formatter:on
	}

}
