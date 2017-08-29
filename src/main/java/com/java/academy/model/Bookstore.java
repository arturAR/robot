package com.java.academy.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "bookstores")
public class Bookstore extends BaseEntity implements Serializable{

	private static final long serialVersionUID = 3678107894576131001L;

	private String name;
	private String url;
	
	public Bookstore() {} //for hibernate

	public Bookstore(String name, String url) {
		this.name = name;
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
