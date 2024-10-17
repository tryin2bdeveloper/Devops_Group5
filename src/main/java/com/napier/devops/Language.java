package com.napier.devops;

public class Language {
    private String language;
    private int population;

    public Language(String language, int population) {
        this.language = language;
        this.population = population;
    }
    public String getLanguage() {return language;}
    public int getPopulation() {return population;}
}
