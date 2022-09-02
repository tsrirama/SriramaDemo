package com.srirama;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import javax.enterprise.event.ObservesAsync;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Path("/palindrome")
public class PalindromeVerifier {
    private static  Map<Character, List<String>> palindromeCache = null;
    private static  Map<Character, List<String>> nonPalindromeCache = null;
    private String[] invalidInputs = {"/^$|\\s+/", ".*[0-9].*"};
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
    @Path("/{userName}/{input}")
    @Produces(MediaType.TEXT_PLAIN)
    @Operation(summary = "Palindrome Checker", description = "Checks if given value is Palindrome and stores the same")
    @APIResponses(value = @APIResponse(responseCode = "200", description = "Success",
            content = @Content(mediaType = "text/plain")))
    public String isPalindrome(@PathParam("userName") String userName, @PathParam("input")  String input) {
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

                    }
                }
                updatePalindromeCacheStore(firstChar,input);
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

                    }
                }
                updateNonPalindromeCacheStore(firstChar,input);
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
                    newLines.add(key+"="+vals[1]+","+String.valueOf(input));
                }else{
                    newLines.add(line);
                }
            }
            if(!foundLine){
                newLines.add(key+"="+String.valueOf(input));
            }
            Files.write(f.toPath(), newLines, Charset.defaultCharset());
        }catch(IOException ex){
           System.out.println(ex);
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
            if(input.matches(s)){
                return true;
            }
        }
        return false;
    }
}