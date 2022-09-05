package com.srirama;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class PalindromeVerifierTest {

    @Test
    public void testWithPalindrome() {
        given()
                .when().get("/palindrome/sriram/abcba")
                .then()
                .statusCode(200)
                .body(is("Hello sriram ABCBA is a palindrome"));
    }
    @Test
    public void testCacheUpdate() {
        given()
                .when().get("/palindrome/sriram/ahddha")
                .then()
                .statusCode(200)
                .body(is("Hello sriram AHDDHA is a palindrome"));
    }
    @Test
    public void testNonPalindrome() {
        given()
                .when().get("/palindrome/sriram/abrfhh")
                .then()
                .statusCode(200)
                .body(is("Hello sriram ABRFHH is not a palindrome"));
    }
    @Test
    public void testWithNumbers() {
        given()
                .when().get("/palindrome/sriram/135abba531")
                .then()
                .statusCode(200)
                .body(is("Invalid Input.Please enter a valid input"));
    }
    @Test
    public void testWithSpace() {
        given()
                .when().get("/palindrome/sriram/ab%20ba")
                .then()
                .statusCode(200)
                .body(is("Invalid Input.Please enter a valid input"));
    }
    @Test
    public void testValidateInputWithNumbers() {
       PalindromeVerifier pv = new PalindromeVerifier();
       boolean  result = pv.validateInput("ds12abc");
       assert(result);

    }

    @Test
    public void testValidateInputWithSpaces() {
        PalindromeVerifier pv = new PalindromeVerifier();
        boolean  result = pv.validateInput("abc cba");
        assert(result);
    }

    @Test
    public void testCheckPalindrome() {
        PalindromeVerifier pv = new PalindromeVerifier();
        boolean  result = pv.checkPalindrome("abccba");
        assert(result);
    }

    @Test
    public void testCheckPalindromeFalse() {
        PalindromeVerifier pv = new PalindromeVerifier();
        boolean  result = pv.checkPalindrome("abccdsba");
        assert(!result);
    }


}