package com.napier.devops;

public class City {
    private String name;
    private String countryName;  // New field for Country Name
    private String district;
    private int population;

    // Constructor
    public City(String name, String countryName, String district, int population) {
        this.name = name;
        this.countryName = countryName;
        this.district = district;
        this.population = population;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getDistrict() {
        return district;
    }

    public int getPopulation() {
        return population;
    }

}