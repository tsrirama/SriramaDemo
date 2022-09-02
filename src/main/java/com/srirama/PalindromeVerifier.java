package com.srirama;

import javax.enterprise.event.ObservesAsync;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Path("/palindrome")
public class PalindromeVerifier {
    private static  Map<Character, List<String>> palindromeCache = null;
    private static  Map<Character, List<String>> nonPalindromeCache = null;
    private String[] invalidInputs = {"/^$|\\s+/", "\\d{1,}"};
    static{
        loadCache();
    }

    private static void loadCache(){
        palindromeCache = getMapFromDataStore("polindromeMap.txt");
        nonPalindromeCache = getMapFromDataStore("nonPolindromeMap.txt");
    }
    private static Map<Character, List<String>> getMapFromDataStore(String datasource){
        BufferedReader cacheDataSource;
        Map<Character, List<String>> cacheMap = new HashMap<Character,List<String>>();
        String cacheLine;
        char key;
        String value;
        try{
            cacheDataSource = new BufferedReader(new FileReader("/datasource"));
            while(null != (cacheLine = cacheDataSource.readLine())){
                String[] keyValues = cacheLine.split("=");
                key = keyValues[0].charAt(0);
                value = keyValues[1];
                StringTokenizer values = new StringTokenizer(value,",");
                List<String> valuesList = new ArrayList<String>();
                while(values.hasMoreTokens()){
                    valuesList.add(values.nextToken());
                }
                cacheMap.put(key,valuesList);
            }

        }catch(IOException ex){
            return null;
        }
        return cacheMap;
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String isPalindrome(String userName,String input) {
        boolean isPalindrome = false;
        if(validateInput(input)){
            return "Invalid Input.Please enter a valid input";
        }else{
            input = input.toUpperCase();
            char firstChar = input.charAt(0);
            if(null != palindromeCache){
                List cacheList = palindromeCache.get(firstChar);
                if(null != cacheList){
                    if(cacheList.contains(input)){
                       return "Hello"+userName+" "+input+" "+"is a palindrome";
                    }
                }
            }
            if(null != nonPalindromeCache){
                List cacheList = nonPalindromeCache.get(firstChar);
                if(null != cacheList){
                    if(cacheList.contains(input)){
                        return "Hello"+userName+" "+input+" "+"is not a palindrome";
                    }
                }
            }
            isPalindrome = checkPalindrome(input);

            if(isPalindrome){
                if(null != palindromeCache){
                    List cacheList = palindromeCache.get(firstChar);
                    if(null == cacheList){
                        cacheList = new ArrayList<String>();
                    }
                    if(null != cacheList){
                        cacheList.add(input);
                        palindromeCache.put(firstChar,cacheList);
                        updatePalindromeCacheStore(firstChar,input);
                    }
                }
                return "Hello"+userName+" "+input+" "+"is a palindrome";
            }else{
                if(null != nonPalindromeCache){
                    List cacheList = nonPalindromeCache.get(firstChar);
                    if(null == cacheList){
                        cacheList = new ArrayList<String>();
                    }
                    if(null != cacheList){
                        cacheList.add(input);
                        nonPalindromeCache.put(firstChar,cacheList);
                        updateNonPalindromeCacheStore(firstChar,input);
                    }
                }
                return "Hello"+userName+" "+input+" "+"is not a palindrome";
            }
        }
    }

    private void updatePalindromeCacheStore(char key,String input){
        CompletableFuture.runAsync(() -> {
                updateFile(key,input,"polindromeMap.txt");
        });
    }
    private void updateNonPalindromeCacheStore(char key,String input){
        CompletableFuture.runAsync(() -> {
            updateFile(key,input,"nonPolindromeMap.txt");
        });
    }
    private void updateFile(char key,String input,String fileName){
        List<String> lines;
        try {
            File f = new File(fileName);
            lines = Files.readAllLines(f.toPath(), Charset.defaultCharset());
            List<String> newLines = new ArrayList<String>();
            for(String line: lines){
                    String [] vals = line.split("=");
                    if(vals[0].charAt(0) == key){
                    newLines.add(vals[1]+","+String.valueOf(input));
                }else{
                    newLines.add(line);
                }
            }
            Files.write(f.toPath(), newLines, Charset.defaultCharset());
        }catch(IOException ex){
           // filed to update persistent cache
        }
    }
private boolean checkPalindrome(String input){
        char[] chars = input.toCharArray();
        int length = chars.length;
        int index1=0;
        int index2 =length-1;
        while(index1 <index2){
            if(chars[index1] != chars[index2]){
                return false;
            }
            index1++;index2--;
        }
        return true;
}
    private boolean validateInput(String input){
        for (String s : invalidInputs) {
            return input.matches(s);
        }
        return false;
    }
}