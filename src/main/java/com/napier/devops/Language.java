package com.napier.devops;

public class Language {
    private final String language;
    private final int population;
    private final int percentage;

    public Language(String language, int population, long tot_population, int percentage) {
        this.language = language;
        this.population = population;
        this.percentage = percentage; // Correct assignment
    }

    public String getLanguage() {return language;}
    public int getPopulation() {return population;}
    public int getPercentage() {return  percentage;}
}
