package com.napier.devops;

import java.util.List;

public class Country {
    private final String Code;  // Country Code
    private final String name;         // Country Name
    private final String continent;    // Continent
    private final String region;       // Region
    private final int population;       // Population
    private final String capital;      // Capital

    // Constructor to initialize all fields
    public Country(String countryCode, String name, String continent, String region, int population, String capital) {
        this.Code = countryCode;
        this.name = name;
        this.continent = continent;
        this.region = region;
        this.population = population;
        this.capital = capital;
    }

    // Getters for all fields
    public String getCountryCode() {
        return Code;
    }

    public String getName() {
        return name;
    }

    public String getContinent() {
        return continent;
    }

    public String getRegion() {
        return region;
    }

    public int getPopulation() {
        return population;
    }

    public String getCapital() {
        return capital;
    }
}

