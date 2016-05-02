package com.yuji.tdb.db;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class KeyValue {
    @PrimaryKey
    @Persistent
    private String key;
    @Persistent
	private String value;
	
    public KeyValue(String key, String value){
    	this.key = key;
    	this.value = value;
    }
    
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}
