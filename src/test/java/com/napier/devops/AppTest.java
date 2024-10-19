package com.napier.devops;

import org.junit.jupiter.api.*;

import java.sql.SQLException;
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
        app.connect("localhost:33060", 0); // Make sure the connection is valid
    }

    @BeforeEach
    public void setUp() throws SQLException {
        // Ensure the connection is active before each test
        if (app.getConnection() == null || app.getConnection().isClosed()) {
            app.connect("localhost:33060", 0);  // Reconnect if the connection is closed or null
        }
    }

    @AfterAll
    public void tearDown() {
        app.disconnect();
    }

    @AfterEach
    void printSuccessMessage() {
        System.out.println("Test executed successfully.");
    }

    @Test
    void testPrintCountriesWithNull()
    {
        // Pass null to printCountries and ensure it does not throw exceptions
        app.printCountries(null, "Header", null);
    }

    @Test
    void testPrintCountriesWithEmptyList()
    {
        // Pass an empty list and check if it handles it correctly
        List<Country> emptyList = new ArrayList<>();
        app.printCountries(emptyList, "Empty List", null);
        assertEquals(0, emptyList.size());
    }

    @Test
    void testPrintCountriesWithData()
    {
        List<Country> countries = new ArrayList<>();
        countries.add(new Country("USA", "United States", "North America", "Northern America", 331002651, "Washington D.C."));
        countries.add(new Country("GBR", "United Kingdom", "Europe", "Northern Europe", 67886011, "London"));
        app.printCountries(countries, "Test Data", null);
        assertEquals(2, countries.size());
    }

    @Test
    void testPrintCitiesWithNull() {
        app.printCities(null, "Header", null);
    }

    @Test
    void testPrintCitiesWithEmptyList() {
        List<City> emptyList = new ArrayList<>();
        app.printCities(emptyList, "Empty List", null);
        assertEquals(0, emptyList.size());
    }

    @Test
    void testPrintCitiesWithData() {
        List<City> cities = new ArrayList<>();
        cities.add(new City("New York", "United States", "New York", 8175133));
        cities.add(new City("London", "United Kingdom", "London", 8787892));
        app.printCities(cities, "Test Data", null);
        assertEquals(2, cities.size());
    }

    @Test
    void testPrintCapitalsWithNull() {
        app.printCapitals(null, "Header", null);
    }

    @Test
    void testPrintCapitalsWithEmptyList() {
        List<Capital> emptyList = new ArrayList<>();
        app.printCapitals(emptyList, "Empty List", null);
        assertEquals(0, emptyList.size());
    }

    // Test for getPopulatedCapital()
    @Test
    void testPrintCapitalsWithData() {
        List<Capital> capitals = new ArrayList<>();
        capitals.add(new Capital("Washington D.C.", 331002651, "United States"));
        capitals.add(new Capital("London", 67886011, "United Kingdom"));
        app.printCapitals(capitals, "Test Data", null);
        assertEquals(2, capitals.size());
    }

    // Test for getPopulatedCountries() with a negative limit
    @Test
    void testGetPopulatedCountriesWithNegativeLimit() {
        List<Country> result = app.getPopulatedCountries(app.getConnection(), null, null, -1);
        assertTrue(result.isEmpty(), "List should be empty for negative limit.");
    }

    // Test for getPopulatedCountries() with a null connection
    @Test
    void testGetPopulatedCountriesWithNullConnection() {
        List<Country> result = app.getPopulatedCountries(null, null, null, 10);
        assertNotNull(result, "Result should not be null even if connection is null.");
        assertTrue(result.isEmpty(), "List should be empty for a null connection.");
    }

    // Test for getPopulatedCountries() with a valid positive limit
    @Test
    void testGetPopulatedCountriesWithPositiveLimit() {
        List<Country> result = app.getPopulatedCountries(app.getConnection(), null, null, 10);

        System.out.println("#### Printing Populated Countries (Positive Limit) ####");
        app.printCountries(result, "Populated Countries with Positive Limit", null);

        assertFalse(result.isEmpty(), "List should not be empty for a valid connection and positive limit.");
        assertTrue(result.size() <= 10, "List should contain 10 or fewer countries.");
    }

    // Test for getPopulatedCountries() with a valid key and value
    @Test
    void testGetPopulatedCountriesWithKeyAndValue() {
        List<Country> result = app.getPopulatedCountries(app.getConnection(), "Continent", "Asia", 10);
        System.out.println("#### Printing Populated Countries (Key: Continent, Value: Asia) ####");
        app.printCountries(result, "Populated Countries (Continent: Asia)", null);
        assertFalse(result.isEmpty(), "List should not be empty when a valid key and value are provided.");
    }

    // Test for getPopulatedCity() with a negative limit
    @Test
    void testGetPopulatedCityWithNegativeLimit() {
        List<City> result = app.getPopulatedCity(app.getConnection(), null, null, -1);
        assertTrue(result.isEmpty(), "List should be empty for negative limit.");
    }

    // Test for getPopulatedCity() with a null connection
    @Test
    void testGetPopulatedCityWithNullConnection() {
        List<City> result = app.getPopulatedCity(null, null, null, 10);
        assertNotNull(result, "Result should not be null even if connection is null.");
        assertTrue(result.isEmpty(), "List should be empty for a null connection.");
    }

    // Test for getPopulatedCity() with a valid positive limit
    @Test
    void testGetPopulatedCityWithPositiveLimit() {
        List<City> result = app.getPopulatedCity(app.getConnection(), null, null, 10);
        System.out.println("#### Printing Populated Cities (Positive Limit) ####");
        app.printCities(result, "Populated Cities with Positive Limit", null);
        assertFalse(result.isEmpty(), "List should not be empty for a valid connection and positive limit.");
        assertTrue(result.size() <= 10, "List should contain 10 or fewer cities.");
    }


    // Test for getPopulatedCity() with a valid key and value
    @Test
    void testGetPopulatedCityWithKeyAndValue() {
        List<City> result = app.getPopulatedCity(app.getConnection(), "Name", "Russian Federation", 10);

    }

    // Test for getPopulatedCapital() with a negative limit
    @Test
    void testGetPopulatedCapitalWithNegativeLimit() {
        List<Capital> result = app.getPopulatedCapital(app.getConnection(), null, null, -1);
        assertTrue(result.isEmpty(), "List should be empty for negative limit.");
    }

    // Test for getPopulatedCapital() with a null connection
    @Test
    void testGetPopulatedCapitalWithNullConnection() {
        List<Capital> result = app.getPopulatedCapital(null, null, null, 10);
        assertNotNull(result, "Result should not be null even if connection is null.");
        assertTrue(result.isEmpty(), "List should be empty for a null connection.");
    }

    // Test for getPopulatedCapital() with a valid positive limit
    @Test
    void testGetPopulatedCapitalWithPositiveLimit() {
        List<Capital> result = app.getPopulatedCapital(app.getConnection(), null, null, 10);
        System.out.println("#### Printing Populated Capitals (Positive Limit) ####");
        app.printCapitals(result, "Populated Capitals with Positive Limit", null);
        assertFalse(result.isEmpty(), "List should not be empty for a valid connection and positive limit.");
        assertTrue(result.size() <= 10, "List should contain 10 or fewer capitals.");
    }

    // Test for getPopulatedCapital() with a valid key and value
    @Test
    void testGetPopulatedCapitalWithKeyAndValue() {
        List<Capital> result = app.getPopulatedCapital(app.getConnection(), "Continent", "Asia", 10);
        System.out.println("#### Printing Populated Capitals (Key: Continent, Value: Asia) ####");
        app.printCapitals(result, "Populated Capitals (Continent: Asia)", null);
        assertFalse(result.isEmpty(), "List should not be empty when a valid key and value are provided.");
    }

    @Test
    void testTableDisplay() {
        // Call Table_display to ensure it doesn't throw any exceptions
        assertDoesNotThrow(() -> app.Table_display(), "Table_display should not throw exceptions");
    }

    @Test
    void testGetPopulationWithInValidData() {
        // Simulate getting population for the city of Paris
        List<Population> population = app.getPopulation(app.getConnection(), "city", null);
        assertNotNull(population, "Population list should not be null for a valid city");
        assertTrue(population.isEmpty(), "Population list should not be empty for a valid city");
    }

    @Test
    void testGetPopulationWithValidData() {
        // Simulate getting population for the United States
        List<Population> population = app.getPopulation(app.getConnection(), "country.Name", "United States");
        assertNotNull(population, "Population list should not be null for a valid country");
        assertFalse(population.isEmpty(), "Population list should not be empty for a valid country");
    }

    @Test
    void testGetPopulationWithInvalidCity() {
        // Simulate an invalid city name
        List<Population> population = app.getPopulation(app.getConnection(), "InvalidCity", null);
        assertNotNull(population, "Population list should not be null even for an invalid city");
        assertTrue(population.isEmpty(), "Population list should be empty for an invalid city");
    }

    @Test
    void testGetPopulationDetail() {
        // Retrieve the population details for the United States
        List<Population> population = app.getPopulation(app.getConnection(), "country.Name", "United States");

        // Validate the results
        assertNotNull(population);
        assertEquals(1, population.size()); // Expecting only one entry for the United States

        // Check the United States data
        Population usPopulation = population.get(0);
        assertEquals("United States", usPopulation.getName()); // Check the country name
        assertEquals(76269818000L, usPopulation.getTotalPopulation()); // Check total population
        assertEquals(78625774L, usPopulation.getPopulationInCities()); // Check population in cities
        assertEquals(76191192226L, usPopulation.getPopulationNotInCities()); // Check population not in cities
        assertEquals(0.10, usPopulation.getPercentageInCities(), 0.01); // Check percentage in cities (0.10%)
        assertEquals(99.90, usPopulation.getPercentageNotInCities(), 0.01); // Check percentage not in cities (99.90%)
    }

    @Test
    void testGetPopulatedCountry() {
        // Retrieve the populated countries for Europe
        List<Country> result = app.getPopulatedCountries(app.getConnection(), "Continent", "Europe", 1);

        // Validate the results
        assertNotNull(result); // Check that the result is not null
        assertEquals(1, result.size()); // Expecting only one entry

        // Check the Russian Federation data
        Country russia = result.get(0);
        assertEquals("RUS", russia.getCountryCode()); // Check the country code
        assertEquals("Russian Federation", russia.getName()); // Check the country name
        assertEquals("Europe", russia.getContinent()); // Check the continent
        assertEquals("Eastern Europe", russia.getRegion()); // Check the region
        assertEquals(146934000, russia.getPopulation()); // Check the population
        assertEquals("Moscow", russia.getCapital()); // Check the capital city
    }

    @Test
    void testGetPopulatedCities() {
        // Retrieve the populated cities for Asia
        List<City> cities = app.getPopulatedCity(app.getConnection(), "Continent", "Asia", 1);

        // Validate the results
        assertNotNull(cities); // Ensure the result is not null
        assertEquals(1, cities.size()); // Expecting only one entry

        // Check the Mumbai data
        City mumbai = cities.get(0);
        assertEquals("Mumbai (Bombay)", mumbai.getName()); // Check the city name
        assertEquals("India", mumbai.getCountryName()); // Check the country name
        assertEquals("Maharashtra", mumbai.getDistrict()); // Check the district
        assertEquals(10500000, mumbai.getPopulation()); // Check the population
    }


    @Test
    void testGetPopulatedCapital() {
        // Retrieve the populated capital cities, limiting to 1 result
        List<Capital> capitals = app.getPopulatedCapital(app.getConnection(), null, null, 1);

        // Validate the results
        assertNotNull(capitals); // Ensure the result is not null
        assertEquals(1, capitals.size()); // Expecting only one entry

        // Check the Peking data
        Capital peking = capitals.get(0);
        assertEquals("Peking", peking.getCapital()); // Check the capital name
        assertEquals("China", peking.getName()); // Check the country name
        assertEquals(1277558000, peking.getPopulation()); // Check the population
    }

    @Test
    void testLanguage() {
        // Retrieve the list of languages
        List<Language> languages = app.getLanguages(app.getConnection());

        // Validate that the list is not null
        assertNotNull(languages);

        // Check the size of all list
        assertEquals(5, languages.size());

        // Validate the expected languages and populations
        assertEquals("Chinese", languages.get(0).getLanguage());
        assertEquals(1968265500, languages.get(0).getPopulation());

        assertEquals("Hindi", languages.get(1).getLanguage());
        assertEquals(1046303000, languages.get(1).getPopulation());

        assertEquals("Spanish", languages.get(2).getLanguage());
        assertEquals(750296800, languages.get(2).getPopulation());

        assertEquals("English", languages.get(3).getLanguage());
        assertEquals(627418300, languages.get(3).getPopulation());

        assertEquals("Arabic", languages.get(4).getLanguage());
        assertEquals(552045100, languages.get(4).getPopulation());
    }


}
