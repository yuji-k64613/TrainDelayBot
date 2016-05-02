package com.yuji.tdb.db;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;

public class KeyValueDao {
	public static final String KEY_CONSUMER_KEY = "CONSUMER_KEY";
	public static final String KEY_CONSUMER_SECRET = "CONSUMER_SECRET";
	public static final String KEY_REQUEST_TOKEN = "REQUEST_TOKEN";
	public static final String KEY_REQUEST_TOKEN_SECRET = "REQUEST_TOKEN_SECRET";
	public static final String KEY_ACCESS_TOKEN = "ACCESS_TOKEN";
	public static final String KEY_ACCESS_TOKEN_SECRET = "ACCESS_TOKEN_SECRET";

	public static final String KEY_SEARCH_PERIOD = "SEARCH_PERIOD";
	public static final String KEY_TWIT_PERIOD = "TWIT_PERIOD";
	public static final String KEY_TWIT_TIME = "TWIT_TIME_";

	private static KeyValueDao instance = null;
	private PersistenceManager pm = PMFactory.get().getPersistenceManager();
		
	public static KeyValueDao getInstance(){
		if (instance == null){
			instance = new KeyValueDao();
		}
		return instance;
	}
	
	private KeyValueDao(){
		
	}
	
	public String get(String key){
		KeyValue keyValue = null;
		
		try {
			keyValue = pm.getObjectById(KeyValue.class, key);
		}
		catch (JDOObjectNotFoundException e){
			return null;			
		}
		return keyValue.getValue();
	}
	
	public int getInt(String key){
		String value = get(key);
		return Integer.valueOf(value).intValue();
	}
	
	public void put(String key, String value){
		KeyValue keyValue = new KeyValue(key, value);
		pm.makePersistent(keyValue);
	}

	public void put(String key, int value){
		put(key, String.valueOf(value));
	}

	public String get(String key, String initValue){
		String value = get(key);
		if (value == null){
			put(key, initValue);
			value = initValue;
		}
		return value;
	}

	public int getInt(String key, int initValue){
		String value = get(key);
		if (value == null){
			put(key, initValue);
			return initValue;
		}
		return Integer.valueOf(value).intValue();
	}
}
