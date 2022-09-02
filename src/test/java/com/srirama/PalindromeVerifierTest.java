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
                .body(is("Hellosriram ABCBA is a palindrome"));
    }
    @Test
    public void testCacheUpdate() {
        given()
                .when().get("/palindrome/sriram/ahddha")
                .then()
                .statusCode(200)
                .body(is("Hellosriram AHDDHA is a palindrome"));
    }
    @Test
    public void testNonPalindrome() {
        given()
                .when().get("/palindrome/sriram/abrfhh")
                .then()
                .statusCode(200)
                .body(is("Hellosriram ABRFHH is not a palindrome"));
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

}