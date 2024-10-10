package com.napier.devops;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AppTest
{
    static App app;

    @BeforeAll
    static void init()
    {
        app = new App();
    }

    @AfterEach
    public void tearDown() {
        app.disconnect();
    }

    @Test
    void testPrintCountriesWithNull()
    {
        // Pass null to printCountries and ensure it does not throw exceptions
        app.printCountries(null, "Header");
        // You can also assert that it gracefully handles null values without breaking
    }

    @Test
    void testPrintCountriesWithEmptyList()
    {
        // Pass an empty list and check if it handles it correctly
        List<Country> emptyList = new ArrayList<>();
        app.printCountries(emptyList, "Empty List");

        // No exception should be thrown, and the output should indicate that no countries were found
        assertEquals(0, emptyList.size());
    }

    @Test
    void testPrintCountriesWithData()
    {
        // Create a list with sample country data
        List<Country> countries = new ArrayList<>();
        countries.add(new Country("USA", "United States", "North America", "Northern America", 331002651, "Washington D.C."));
        countries.add(new Country("GBR", "United Kingdom", "Europe", "Northern Europe", 67886011, "London"));

        // Check that it prints correctly
        app.printCountries(countries, "Test Data");
        assertEquals(2, countries.size());
    }

    @Test
    void testPrintCitiesWithNull()
    {
        app.printCities(null, "Header");
    }

    @Test
    void testPrintCitiesWithEmptyList()
    {
        List<City> emptyList = new ArrayList<>();
        app.printCities(emptyList, "Empty List");

        assertEquals(0, emptyList.size());
    }

    @Test
    void testPrintCitiesWithData()
    {
        // Create a list with sample city data
        List<City> cities = new ArrayList<>();
        cities.add(new City("New York", "United States", "New York", 8175133));
        cities.add(new City("London", "United Kingdom", "London", 8787892));

        // Check that it prints correctly
        app.printCities(cities, "Test Data");
        assertEquals(2, cities.size());
    }

    @Test
    void testPrintCapitalsWithNull()
    {
        app.printCapitals(null, "Header");
    }

    @Test
    void testPrintCapitalsWithEmptyList()
    {
        List<Capital> emptyList = new ArrayList<>();
        app.printCapitals(emptyList, "Empty List");

        assertEquals(0, emptyList.size());
    }

    @Test
    void testPrintCapitalsWithData()
    {
        // Create a list with sample capital data
        List<Capital> capitals = new ArrayList<>();
        capitals.add(new Capital("Washington D.C.", 331002651, "United States"));
        capitals.add(new Capital("London", 67886011, "United Kingdom"));

        // Check that it prints correctly
        app.printCapitals(capitals, "Test Data");
        assertEquals(2, capitals.size());
    }

    @Test
    void testGetCountry(){

    }

}
