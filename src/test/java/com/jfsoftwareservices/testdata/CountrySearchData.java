package com.jfsoftwareservices.testdata;

public record CountrySearchData(String countryCode, String countryName) {

    public static final CountrySearchData DEFAULT = new CountrySearchData("ind", "India");
}
