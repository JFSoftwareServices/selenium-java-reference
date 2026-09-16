package com.jfsoftwareservices.automation.testdata;

import org.testng.annotations.DataProvider;

/**
 * Central data-provider registry. Keeping providers separate from both the
 * data records and the test classes lets new checkout/search combinations be
 * added in one place without touching test logic - the data-driven half of
 * the "Java + TestNG data-driven framework" brief.
 */
public final class DataProviders {

    private DataProviders() {
    }

    @DataProvider(name = "checkoutJourneys")
    public static Object[][] checkoutJourneys() {
        return new Object[][] {
                { OrderTestData.DEFAULT, CountrySearchData.DEFAULT },
        };
    }

    @DataProvider(name = "invalidCredentials")
    public static Object[][] invalidCredentials() {
        return new Object[][] {
                { "invalid@example.com", "invalidpassword" },
        };
    }
}