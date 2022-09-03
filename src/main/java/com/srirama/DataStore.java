package com.srirama;
import java.util.*;

public interface DataStore {

    Map<Character, List<String>> getPalindromeMap();
    Map<Character, List<String>> getNonPalindromeMap();
    void updateCacheAndStore(Character key,String value,boolean isPalindrome);

}
