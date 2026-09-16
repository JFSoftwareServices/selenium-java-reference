package com.jfsoftwareservices.testdata;

public record OrderTestData(String productName, double productPrice) {

    public static final OrderTestData DEFAULT = new OrderTestData("ADIDAS ORIGINAL", 11500);
}