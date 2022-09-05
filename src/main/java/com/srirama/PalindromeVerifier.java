package com.srirama;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Path("/palindrome")
public class PalindromeVerifier {
    private static DataStore ds = DataStoreFactory.getFileDataStore();
    private static  Map<Character, List<String>> palindromeCache = null;
    private static  Map<Character, List<String>> nonPalindromeCache = null;
    private String[] invalidInputs = {"/^S+$/", ".*[0-9].*"};

    static{
        loadCache();
    }
    private static void loadCache(){
        palindromeCache = ds.getPalindromeMap();
        nonPalindromeCache = ds.getNonPalindromeMap();
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
                        return sendResponse(userName,input,true);
                    }
                }
            }
            if(null != nonPalindromeCache){
                List cacheList = nonPalindromeCache.get(firstChar);
                if(null != cacheList){
                    if(cacheList.contains(input)){
                        return sendResponse(userName,input,false);
                    }
                }
            }
            isPalindrome = checkPalindrome(input);
            if(isPalindrome){
                ds.updateCacheAndStore(firstChar,input,true);
                return sendResponse(userName,input,true);
            }else{
                ds.updateCacheAndStore(firstChar,input,false);
                return sendResponse(userName,input,false);
            }
        }
    }

    private String sendResponse(String userName,String input,boolean isPalindrome){
        StringBuffer message = new StringBuffer("Hello ");
        message.append(userName);
        message.append(" ");
        message.append(input);
        if(isPalindrome)
            message.append(" is a ");
        else
            message.append(" is not a ");
        message.append("palindrome");
        return message.toString();
    }

    boolean checkPalindrome(String input){
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
    boolean validateInput(String input){
        for (String s : invalidInputs) {
            if(input.matches(s)){
                return true;
            }
        }
        return false;
    }
}