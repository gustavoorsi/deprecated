package com.referaice.model;

import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;

@MappedSuperclass
public abstract class BaseEntity<ID> {

	@Id
	private ID id;

	private DateTime creationTime;

	private DateTime modificationTime;

	@Version
	private long version;

	public ID getId() {
		return id;
	}

	public DateTime getCreationTime() {
		return creationTime;
	}

	public DateTime getModificationTime() {
		return modificationTime;
	}

	public long getVersion() {
		return version;
	}

	@PrePersist
	public void prePersist() {
		DateTime now = DateTime.now();
		this.creationTime = now;
		this.modificationTime = now;
	}

	@PreUpdate
	public void preUpdate() {
		this.modificationTime = DateTime.now();
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this, false);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}