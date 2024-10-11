package com.napier.devops;


public class Capital {
    private String name;
    private String capital;
    private int population;

//    Constructor
    public Capital(String countryName, int  population, String capitalName) {
        this.name = countryName;
        this.population = population;
        this.capital = capitalName;
    }

//    getter
    public String getName() {
        return name;
    }
    public String getCapital() {
        return capital;
    }
    public int getPopulation() {
        return population;
    }
}
