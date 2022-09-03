package com.srirama;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;
public class FileDataStore implements DataStore{

    private static FileDataStore fileDataStore = null;
    private String palindromeFile = "polindromeMap.txt";
    private String nonPalindromeFile = "nonPolindromeMap.txt";

    private Map<Character, List<String>> palindromeMap = new HashMap<Character, List<String>>();
    private Map<Character, List<String>> nonPalindromeMap = new HashMap<Character, List<String>>();


    public static  FileDataStore getInstance(){
        if(null== fileDataStore){
            fileDataStore = new FileDataStore();
        }
        fileDataStore.loadCache();
        return fileDataStore;
    }

    private void loadCache() {
        loadPalindromeCache();
        loadNonPalindromeCache();
    }

    private void loadPalindromeCache(){
        BufferedReader file;
        try {
            file = new BufferedReader(new FileReader(palindromeFile));
            loadMapFromFile(file,palindromeMap);
        }catch(IOException ex){
            System.out.println("unable to load the palindrome cache : "+ex );
        }
    }
    private void loadNonPalindromeCache(){
        BufferedReader file;
        try {
            file = new BufferedReader(new FileReader(nonPalindromeFile));
            loadMapFromFile(file,nonPalindromeMap);
        }catch(IOException ex){
            System.out.println("unable to load the non palindrome cache : "+ex );
        }

    }
    private void loadMapFromFile(BufferedReader file,Map<Character, List<String>> map){
        String cacheLine;
        char key;
        String value;
        try{
            while(null != (cacheLine = file.readLine())){
                String[] keyValues = cacheLine.split("=");
                key = keyValues[0].charAt(0);
                value = keyValues[1];
                StringTokenizer values = new StringTokenizer(value,",");
                List<String> valuesList = new ArrayList<String>();
                while(values.hasMoreTokens()){
                    valuesList.add(values.nextToken());
                }
                map.put(key,valuesList);
            }

        }catch(IOException ex){
            System.out.println("unable to load the from file to map : "+ex );
        }
    }

    @Override
    public Map<Character, List<String>> getPalindromeMap() {
        return palindromeMap;
    }

    @Override
    public Map<Character, List<String>> getNonPalindromeMap() {
        return nonPalindromeMap;
    }

    @Override
    public void updateCacheAndStore(Character key,String value,boolean isPalindrome) {
        Map<Character, List<String>> map;
        String file;
            if(isPalindrome){
                map = palindromeMap;
                file = palindromeFile;
            }else{
                map = nonPalindromeMap;
                file=nonPalindromeFile;
            }
            updateCache(key,value,map);
            updateStore(key,value,file);

    }
    private void updateCache(Character key,String value,Map<Character, List<String>> map){
        List cacheList = map.get(key);
        if(null == cacheList){
            cacheList = new ArrayList<String>();
        }
        if(null != cacheList){
            cacheList.add(value);
            map.put(key,cacheList);

        }
    }
    private void updateStore(Character key,String value,String fileName){
        List<String> lines;
        boolean foundLine = false;
        try {
            new File(fileName).createNewFile();
            File f = new File(fileName);

            lines = Files.readAllLines(f.toPath(), Charset.defaultCharset());
            List<String> newLines = new ArrayList<String>();
            for(String line: lines){
                String [] vals = line.split("=");
                if(vals[0].charAt(0) == key){
                    foundLine =true;
                    newLines.add(key+"="+vals[1]+","+String.valueOf(value));
                }else{
                    newLines.add(line);
                }
            }
            if(!foundLine){
                newLines.add(key+"="+String.valueOf(value));
            }
            Files.write(f.toPath(), newLines, Charset.defaultCharset());
        }catch(IOException ex){
            System.out.println(" error writing to persistent cache: "+ex);
        }
    }
}
