package com.p2c.thelife.model;



// POJO - plain old java object
/**
 * Abstract model
 *
 */
public abstract class AbstractModel {
		
	public int id; // primary key
	
	public AbstractModel(int id) {
		this.id = id;
	}
	
}
