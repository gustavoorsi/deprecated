package com.referaice.consants;

public final class ReferaiceConstants {
	
	
	public static final class API {
		public static final String RESOURCE_ID = "api_resource";
		public static final String API_PATH = "/api/v1";
		
		// REFERRING
		public static final String REFERRING = API_PATH + "/referring";
		public static final String GET_REFERRING = "/{email}";
	}
	
	

	public static final class WEB {
		// Spring Boot Actuator services
		public static final String AUTOCONFIG_ENDPOINT = "/autoconfig";
		public static final String BEANS_ENDPOINT = "/beans";
		public static final String CONFIGPROPS_ENDPOINT = "/configprops";
		public static final String ENV_ENDPOINT = "/env";
		public static final String MAPPINGS_ENDPOINT = "/mappings";
		public static final String METRICS_ENDPOINT = "/metrics";
		public static final String SHUTDOWN_ENDPOINT = "/shutdown";
	}

}
