package com.referaice.model;

public class Company extends BaseEntity<String> {

	private String name;
	private String description;

	public Company() {
	}

	public Company(String name, String description) {
		this.name = name;
		this.description = description;
		super.prePersist();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return new StringBuilder().append("Company[").append(getId()).append("], ").append(this.getName()).toString();
	}

}
