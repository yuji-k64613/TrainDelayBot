package com.yuji.tdb.db;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Train {
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;
    @Persistent
    private String name;
	@Persistent
    private String searchWord;
    @Persistent
    private int count;

    public Train(String name, String searchWord, int count){
    	this.name = name;
    	this.searchWord = searchWord;
    	this.count = count;
    }
    
    public Long getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getSearchWord() {
		return searchWord;
	}
	public int getCount() {
		return count;
	}
}
