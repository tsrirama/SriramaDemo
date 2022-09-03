package com.srirama;

public class DataStoreFactory {
    private static DataStore fileDataStore;
    private static DataStore dbDataStore;
    public static DataStore getFileDataStore(){
        if(null == fileDataStore){
            fileDataStore = FileDataStore.getInstance();
        }
        return fileDataStore;
    }

    public static DataStore getDbDataStore(){
        if(null == dbDataStore){
            dbDataStore = DbDataStore.getInstance();
        }
        return dbDataStore;
    }
}
