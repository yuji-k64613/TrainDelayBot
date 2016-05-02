package com.yuji.tdb.db;

import java.util.List;

import javax.jdo.PersistenceManager;

public class TrainDao {
	private static TrainDao instance = null;
	private PersistenceManager pm = PMFactory.get().getPersistenceManager();

	public static TrainDao getInstance() {
		if (instance == null) {
			instance = new TrainDao();
		}
		return instance;
	}

	private TrainDao() {

	}

	public List<Train> search() {
		String query = "SELECT FROM " + Train.class.getName();
		List<Train> list = (List<Train>) pm.newQuery(query).execute();
		return list;
	}

	public void put(Train train) {
		pm.makePersistent(train);
	}
}
