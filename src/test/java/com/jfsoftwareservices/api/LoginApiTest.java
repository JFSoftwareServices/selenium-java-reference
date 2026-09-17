package com.jfsoftwareservices.api;

import com.jfsoftwareservices.config.ConfigReader;
import com.jfsoftwareservices.testdata.DataProviders;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * No browser is involved, so this doesn't extend the UI {@code BaseTest}
 * hierarchy.
 */
public class LoginApiTest {

    private final AuthApi authApi = new AuthApi();

    @Test(groups = "parallel", description = "returns a token for valid credentials")
    public void returnsTokenForValidCredentials() {
        AuthApi.Session session = authApi.login(
                ConfigReader.baseUrl(),
                ConfigReader.testUserEmail(),
                ConfigReader.testUserPassword());

        assertThat(session.token()).isNotBlank();
        assertThat(session.userId()).isNotBlank();
    }

    @Test(groups = "parallel", dataProvider = "invalidCredentials", dataProviderClass = DataProviders.class, description = "returns 401 for invalid credentials")
    public void returns400ForInvalidCredentials(String username, String password) {
        Response response = authApi.loginRaw(ConfigReader.baseUrl(), username, password);

        assertThat(response.statusCode()).isEqualTo(400);
    }
}
