package com.jfsoftwareservices.api;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

/**
 * Used two ways:
 * <ol>
 *   <li>Directly, by the API test suite ({@code tests/api}).</li>
 *   <li>By {@code AuthenticatedTest}, to obtain a token/userId pair which is
 *       then injected into the browser's localStorage - avoiding a real UI
 *       login for every journey that doesn't specifically test login.</li>
 * </ol>
 */
public class AuthApi {

    public record Session(String token, String userId) {
    }

    public Session login(String baseUrl, String username, String password) {
        Response response = given()
                .baseUri(baseUrl)
                .contentType("application/json")
                .body(new LoginRequest(username, password))
                .when()
                .post("/api/ecom/auth/login");

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IllegalStateException(
                    "Login failed with status " + response.statusCode() + ": " + response.asString()
            );
        }

        String token = response.jsonPath().getString("token");
        String userId = response.jsonPath().getString("userId");

        return new Session(token, userId);
    }

    /** Raw response variant, used by negative-path API tests that assert on status/body. */
    public Response loginRaw(String baseUrl, String username, String password) {
        return given()
                .baseUri(baseUrl)
                .contentType("application/json")
                .body(new LoginRequest(username, password))
                .when()
                .post("/api/ecom/auth/login");
    }

    private record LoginRequest(String userEmail, String userPassword) {}
}