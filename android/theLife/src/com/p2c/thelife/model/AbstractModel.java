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
	
	@Override
	public boolean equals(Object model) {
		
		// TODO check that model is the same type?
		if (model instanceof AbstractModel) {
			return this.id == ((AbstractModel)model).id;
		} else {
			return false;
		}
	}
	
}
