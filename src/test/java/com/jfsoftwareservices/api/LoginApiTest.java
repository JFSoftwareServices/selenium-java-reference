package com.jfsoftwareservices.api;

import com.jfsoftwareservices.config.ConfigReader;
import com.jfsoftwareservices.testdata.DataProviders;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * No browser is involved, so this doesn't extend the UI {@code BaseTest}
 * hierarchy. Parallel execution is safe, since each test method uses its own credentials and doesn't mutate any shared state.
 */
public class LoginApiTest {

    private final AuthApi authApi = new AuthApi();

    @Test(description = "returns a token for valid credentials")
    public void returnsTokenForValidCredentials() {
        AuthApi.Session session = authApi.login(
                ConfigReader.baseUrl(),
                ConfigReader.testUserEmail(),
                ConfigReader.testUserPassword());

        assertThat(session.token()).isNotBlank();
        assertThat(session.userId()).isNotBlank();
    }

    @Test(dataProvider = "invalidCredentials", dataProviderClass = DataProviders.class, description = "returns 401 for invalid credentials")
    public void returns400ForInvalidCredentials(String username, String password) {
        Response response = authApi.loginRaw(ConfigReader.baseUrl(), username, password);

        assertThat(response.statusCode()).isEqualTo(400);
    }
}
