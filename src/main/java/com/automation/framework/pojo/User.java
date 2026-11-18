package com.automation.framework.pojo;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class User {

    private int id;
    private String name;
    private String username;
    private String email;
    private Address address;
    private String phone;
    private String website;
    private Company company;


    // ============================================================
    // ADDRESS
    // ============================================================
    @Setter
    @Getter
    public static class Address {

        private String street;
        private String suite;
        private String city;
        private String zipcode;
        private Geo geo;

    }


    // ============================================================
    // GEO
    // ============================================================

    @Setter
    @Getter
    public static class Geo {

        private String lat;
        private String lng;

    }


    // ============================================================
    // COMPANY
    // ============================================================

    @Setter
    @Getter
    public static class Company {

        private String name;
        private String catchPhrase;
        private String bs;

    }
}