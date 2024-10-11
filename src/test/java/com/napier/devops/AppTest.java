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

    @Test
    void testPrintCountriesWithNull()
    {
        // Pass null to printCountries and ensure it does not throw exceptions
        app.printCountries(null, "Header");
    }

    @Test
    void testPrintCountriesWithEmptyList()
    {
        // Pass an empty list and check if it handles it correctly
        List<Country> emptyList = new ArrayList<>();
        app.printCountries(emptyList, "Empty List");
        assertEquals(0, emptyList.size());
    }

    @Test
    void testPrintCountriesWithData()
    {
        List<Country> countries = new ArrayList<>();
        countries.add(new Country("USA", "United States", "North America", "Northern America", 331002651, "Washington D.C."));
        countries.add(new Country("GBR", "United Kingdom", "Europe", "Northern Europe", 67886011, "London"));
        app.printCountries(countries, "Test Data");
        assertEquals(2, countries.size());
    }

    @Test
    void testPrintCitiesWithNull() {
        app.printCities(null, "Header");
    }

    @Test
    void testPrintCitiesWithEmptyList() {
        List<City> emptyList = new ArrayList<>();
        app.printCities(emptyList, "Empty List");
        assertEquals(0, emptyList.size());
    }

    @Test
    void testPrintCitiesWithData() {
        List<City> cities = new ArrayList<>();
        cities.add(new City("New York", "United States", "New York", 8175133));
        cities.add(new City("London", "United Kingdom", "London", 8787892));
        app.printCities(cities, "Test Data");
        assertEquals(2, cities.size());
    }

    @Test
    void testPrintCapitalsWithNull() {
        app.printCapitals(null, "Header");
    }

    @Test
    void testPrintCapitalsWithEmptyList() {
        List<Capital> emptyList = new ArrayList<>();
        app.printCapitals(emptyList, "Empty List");
        assertEquals(0, emptyList.size());
    }

    // Test for getPopulatedCapital()
    @Test
    void testPrintCapitalsWithData() {
        List<Capital> capitals = new ArrayList<>();
        capitals.add(new Capital("Washington D.C.", 331002651, "United States"));
        capitals.add(new Capital("London", 67886011, "United Kingdom"));
        app.printCapitals(capitals, "Test Data");
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
        app.printCountries(result, "Populated Countries with Positive Limit");

        assertFalse(result.isEmpty(), "List should not be empty for a valid connection and positive limit.");
        assertTrue(result.size() <= 10, "List should contain 10 or fewer countries.");
    }

    // Test for getPopulatedCountries() with a valid key and value
    @Test
    void testGetPopulatedCountriesWithKeyAndValue() {
        List<Country> result = app.getPopulatedCountries(app.getConnection(), "Continent", "Asia", 10);
        System.out.println("#### Printing Populated Countries (Key: Continent, Value: Asia) ####");
        app.printCountries(result, "Populated Countries (Continent: Asia)");
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
        app.printCities(result, "Populated Cities with Positive Limit");
        assertFalse(result.isEmpty(), "List should not be empty for a valid connection and positive limit.");
        assertTrue(result.size() <= 10, "List should contain 10 or fewer cities.");
    }

    // Test for getPopulatedCity() with a valid key and value
    @Test
    void testGetPopulatedCityWithKeyAndValue() {
        List<City> result = app.getPopulatedCity(app.getConnection(), "Name", "Russian Federation", 10);
        System.out.println("#### Printing Populated Cities (Key: Name, Value: Russian Federation) ####");
        app.printCities(result, "Populated Cities (Name: Russian Federation)");
        assertFalse(result.isEmpty(), "List should not be empty when a valid key and value are provided.");
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
        app.printCapitals(result, "Populated Capitals with Positive Limit");
        assertFalse(result.isEmpty(), "List should not be empty for a valid connection and positive limit.");
        assertTrue(result.size() <= 10, "List should contain 10 or fewer capitals.");
    }

    // Test for getPopulatedCapital() with a valid key and value
    @Test
    void testGetPopulatedCapitalWithKeyAndValue() {
        List<Capital> result = app.getPopulatedCapital(app.getConnection(), "Continent", "Asia", 10);
        System.out.println("#### Printing Populated Capitals (Key: Continent, Value: Asia) ####");
        app.printCapitals(result, "Populated Capitals (Continent: Asia)");
        assertFalse(result.isEmpty(), "List should not be empty when a valid key and value are provided.");
    }

    @Test
    void testTableDisplay() {
        // Call Table_display to ensure it doesn't throw any exceptions
        assertDoesNotThrow(() -> app.Table_display(), "Table_display should not throw exceptions");
    }
}
