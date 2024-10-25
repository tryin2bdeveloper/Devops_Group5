package com.napier.devops;

public class Population {
    private final String name;
    private final long totalPopulation;
    private final long populationNotInCities;
    private final long populationInCities;
    private final double percentageInCities;
    private final double percentageNotInCities;

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
