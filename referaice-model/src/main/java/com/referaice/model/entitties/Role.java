package com.referaice.model.entitties;

public enum Role {
    ROLE_USER("ROLE_USER"), 
    ROLE_ADMIN("ROLE_ADMIN"),
    ROLE_CLIENT("ROLE_CLIENT"),
    ROLE_ANONYMOUS("ROLE_ANONYMOUS");
    
    private String name;
    
    private Role( String role ){
    	this.name = role;
    }
    
    public String getName(){
    	return name;
    }

	@Override
	public String toString() {
		return name;
	}

    
}
