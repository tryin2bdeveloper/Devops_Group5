package com.napier.devops;

import org.junit.jupiter.api.*;
import java.sql.SQLException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AppIntegrationTest
{
    static App app;

    @BeforeAll
    static void init()
    {
        app = new App();
        app.connect("localhost:33060", 2000);
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
    void testGetCountryWithNullConnection() {
        List<Country> countries = app.getPopulatedCountries(null, null, null, 0);
        assertNotNull(countries, "Connection to database is not established");
        assertTrue(countries.isEmpty(), "Countries list should be empty when connection is null");
    }

    @Test
    void testGetCityWithNullConnection() {
        List<City> cities = app.getPopulatedCity(null, null, null, 0);
        assertNotNull(cities, "Cities list should not be null");
        assertTrue(cities.isEmpty(), "Cities list should be empty when connection is null");
    }

    @Test
    void testGetCapitalWithNullConnection() {
        List<Capital> capitals = app.getPopulatedCapital(null, null, null, 0);
        assertNotNull(capitals, "Capitals list should not be null");
        assertTrue(capitals.isEmpty(), "Capitals list should be empty when connection is null");
    }

    @Test
    void testGetCountryWithFilter() {
        List<Country> countries = app.getPopulatedCountries(app.getConnection(), "Continent", "Europe", 5);
        app.printCountries(countries, "Test Data");
    }

    @Test
    void testGetCityWithFilter() {
        List<City> cities = app.getPopulatedCity(app.getConnection(), "Name", "Russian Federation", 5);
        app.printCities(cities, "Test Data");
    }

    @Test
    void testGetCapitalWithFilter() {
        List<Capital> capitals = app.getPopulatedCapital(app.getConnection(), "Continent", "Asia", 5);
        app.printCapitals(capitals, "Test Data");
    }

    @Test
    void testGetCountryWithInvalidFilter() {
        List<Country> countries = app.getPopulatedCountries(app.getConnection(), "InvalidKey", "InvalidValue", 5);
        assertNotNull(countries, "Countries list should not be null");
        assertTrue(countries.isEmpty(), "Countries list should be empty when using an invalid filter");
    }

    @Test
    void testGetCityWithInvalidFilter() {
        List<City> cities = app.getPopulatedCity(app.getConnection(), "InvalidKey", "InvalidValue", 5);
        assertNotNull(cities, "Cities list should not be null");
        assertTrue(cities.isEmpty(), "Cities list should be empty when using an invalid filter");
    }

    @Test
    void testGetCapitalWithInvalidFilter() {
        List<Capital> capitals = app.getPopulatedCapital(app.getConnection(), "InvalidKey", "InvalidValue", 5);
        assertNotNull(capitals, "Capitals list should not be null");
        assertTrue(capitals.isEmpty(), "Capitals list should be empty when using an invalid filter");
    }

    @Test
    void testTableDisplayWithValidData() {
        assertDoesNotThrow(() -> app.Table_display(), "Table_display should not throw exceptions for valid data.");
    }

    @Test
    void testTableDisplayWithValidConnection() {
        app.connect("localhost:33060", 0);
        assertDoesNotThrow(() -> app.Table_display(), "Table_display should run without errors for a valid connection.");
    }

    @Test
    void testTableDisplayWithNullConnection() {
        app.disconnect(); // This will set `con` to null
        assertDoesNotThrow(() -> app.Table_display(), "Table_display should handle a null connection gracefully.");
    }
}
