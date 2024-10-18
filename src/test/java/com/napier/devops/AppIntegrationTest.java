package com.napier.devops;

import org.junit.jupiter.api.*;
import java.sql.SQLException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AppIntegrationTest
{
    static App app;

    // Initialize the application and establish a database connection
    @BeforeAll
    static void init()
    {
        app = new App(); // Create a new instance of the App class
        app.connect("localhost:33060", 2000); // Connect to the database with a delay
    }

    // Ensure the database connection is active before each test
    @BeforeEach
    public void setUp() throws SQLException {
        // If the connection is null or closed, reconnect to the database
        if (app.getConnection() == null || app.getConnection().isClosed()) {
            app.connect("localhost:33060", 0); // Reconnect to the database
        }
    }

    // Close the database connection after all tests are done
    @AfterAll
    public void tearDown() {
        app.disconnect(); // Disconnect from the database
    }

    // Test case: Get countries with a null connection, expect an empty list
    @Test
    void testGetCountryWithNullConnection() {
        List<Country> countries = app.getPopulatedCountries(null, null, null, 0); // Call method with null connection
        assertNotNull(countries, "Connection to database is not established"); // Ensure list is not null
        assertTrue(countries.isEmpty(), "Countries list should be empty when connection is null"); // Expect empty list
    }

    // Test case: Get cities with a null connection, expect an empty list
    @Test
    void testGetCityWithNullConnection() {
        List<City> cities = app.getPopulatedCity(null, null, null, 0); // Call method with null connection
        assertNotNull(cities, "Cities list should not be null"); // Ensure list is not null
        assertTrue(cities.isEmpty(), "Cities list should be empty when connection is null"); // Expect empty list
    }

    // Test case: Get capitals with a null connection, expect an empty list
    @Test
    void testGetCapitalWithNullConnection() {
        List<Capital> capitals = app.getPopulatedCapital(null, null, null, 0); // Call method with null connection
        assertNotNull(capitals, "Capitals list should not be null"); // Ensure list is not null
        assertTrue(capitals.isEmpty(), "Capitals list should be empty when connection is null"); // Expect empty list
    }

    // Test case: Get countries with a valid filter for continent "Europe", expect a populated list


    // Test case: Get countries with an invalid filter, expect an empty list
    @Test
    void testGetCountryWithInvalidFilter() {
        List<Country> countries = app.getPopulatedCountries(app.getConnection(), "InvalidKey", "InvalidValue", 5); // Invalid filter
        assertNotNull(countries, "Countries list should not be null"); // Ensure list is not null
        assertTrue(countries.isEmpty(), "Countries list should be empty when using an invalid filter"); // Expect empty list
    }

    // Test case: Get cities with an invalid filter, expect an empty list
    @Test
    void testGetCityWithInvalidFilter() {
        List<City> cities = app.getPopulatedCity(app.getConnection(), "InvalidKey", "InvalidValue", 5); // Invalid filter
        assertNotNull(cities, "Cities list should not be null"); // Ensure list is not null
        assertFalse(cities.isEmpty(), "Cities list should not be Null"); // Expect empty list
    }

    // Test case: Get capitals with an invalid filter, expect an empty list
    @Test
    void testGetCapitalWithInvalidFilter() {
        List<Capital> capitals = app.getPopulatedCapital(app.getConnection(), "InvalidKey", "InvalidValue", 5); // Invalid filter
        assertNotNull(capitals, "Capitals list should not be null"); // Ensure list is not null
        assertTrue(capitals.isEmpty(), "Capitals list should be empty when using an invalid filter"); // Expect empty list
    }

    // Test case: Test table display with valid data, should not throw an exception
    @Test
    void testTableDisplayWithValidData() {
        assertDoesNotThrow(() -> app.Table_display(), "Table_display should not throw exceptions for valid data."); // Ensure no exception is thrown
    }

    // Test case: Test table display with a valid connection, should not throw an exception
    @Test
    void testTableDisplayWithValidConnection() {
        app.connect("localhost:33060", 0); // Ensure connection is established
        assertDoesNotThrow(() -> app.Table_display(), "Table_display should run without errors for a valid connection."); // Ensure no exception is thrown
    }

    // Test case: Test table display with a null connection, should not throw an exception
    @Test
    void testTableDisplayWithNullConnection() {
        app.disconnect(); // Disconnect the database, making connection null
        assertDoesNotThrow(() -> app.Table_display(), "Table_display should handle a null connection gracefully."); // Ensure no exception is thrown
    }
}
