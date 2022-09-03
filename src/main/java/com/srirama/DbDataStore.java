package com.srirama;
import java.util.*;

public class DbDataStore implements DataStore{
    private static DbDataStore dbDataStore = null;

    public static  DbDataStore getInstance(){
        if(null== dbDataStore){
            dbDataStore = new DbDataStore();
        }
        return dbDataStore;
    }
    private DbDataStore(){

    }

    private void loadCache() {

    }

    @Override
    public Map<Character, List<String>> getPalindromeMap() {
        return null;
    }

    @Override
    public Map<Character, List<String>> getNonPalindromeMap() {
        return null;
    }

    @Override
    public void updateCacheAndStore(Character key,String value,boolean isPalindrome) {

    }
}
