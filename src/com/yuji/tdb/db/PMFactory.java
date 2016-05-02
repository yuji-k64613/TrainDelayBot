package com.yuji.tdb.db;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

public class PMFactory {

    private static final PersistenceManagerFactory PMFactoryInstance = JDOHelper
        .getPersistenceManagerFactory("transactions-optional");

    private PMFactory() {
    }

    public static PersistenceManagerFactory get() {
        return PMFactoryInstance;
    }
}