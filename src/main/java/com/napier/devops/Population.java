package com.napier.devops;

public class Population {
    private String name;
    private long totalPopulation;
    private long populationNotInCities;
    private long populationInCities;
    private double percentageInCities;
    private double percentageNotInCities;

    // Constructor
    public Population(String name, long totalPopulation, long populationNotInCities, long populationInCities, double percentageInCities, double percentageNotInCities) {
        this.name = name;
        this.totalPopulation = totalPopulation;
        this.populationNotInCities = populationNotInCities;
        this.populationInCities = populationInCities;
        this.percentageInCities = percentageInCities;
        this.percentageNotInCities = percentageNotInCities;
    }

    // Getters
    public String getName() {
        return name;
    }

    public long getTotalPopulation() {
        return totalPopulation;
    }

    public long getPopulationNotInCities() {
        return populationNotInCities;
    }

    public long getPopulationInCities() {
        return populationInCities;
    }

    public double getPercentageInCities() {
        return percentageInCities;
    }

    public double getPercentageNotInCities() {
        return percentageNotInCities;
    }
}
