package com.napier.devops;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class App {
    /**
     * Connection to MySQL database.
     */
    private Connection con = null;
    /**
     * Connect to the MySQL database.
     */
    public void connect(String location, int delay) {
        try {
            // Load Database driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Could not load SQL driver");
            System.exit(-1);
        }
        int retries = 10;
        for (int i = 0; i < retries; ++i) {
            System.out.println("Connecting to database...");
            try {
                // Wait a bit for db to start
                Thread.sleep(delay);
                // Connect to database
                con = DriverManager.getConnection("jdbc:mysql://" + location + "/world?useSSL=false&allowPublicKeyRetrieval=true", "root", "group-5");
                System.out.println("Successfully connected");
                break;
            } catch (SQLException sqle) {
                System.out.println("Failed to connect to database attempt ");
                System.out.println(sqle.getMessage());
            } catch (InterruptedException ie) {
                System.out.println("Thread interrupted? Should not happen.");
            }
        }
    }

    /**
     * Getter for the database connection.
     *
     * @return The Connection object if connected, null if not connected.
     */
    public Connection getConnection() {
        return con;  // Return the connection object
    }

    /**
     * Disconnect from the MySQL database.
     */
    public void disconnect() {
        // Check if the connection  is establish
        if (con != null) {
            try {
                // Close connection
                con.close();
            } catch (Exception e) {
                System.out.println("Error closing connection to database");
            }
        }
        else {
            // If the connection is already null, indicate that no connection was active
            System.out.println("No database connected");
        }
    }

    public static List<Country> getPopulatedCountries(Connection con, String key, String value, int limit) {
        // Validate that limit must be a positive number
        if (limit < 0) {
            System.out.println("Limit must be a positive integer.");
            return new ArrayList<>();  // Return an empty list if limit is invalid
        }else{
            // Construct the base query
            String Country_query = "SELECT country.Code, country.Name, country.Continent, country.Region, country.Population, city.Name AS Capital " +
                    "FROM country JOIN city ON country.Capital = city.ID ";
            // Add conditions if key and value are provided
            if (key != null && value != null) {
                Country_query += "WHERE country." + key + " = '" + value + "' ";
            }
            // Append ORDER BY clause and limit if required
            Country_query += "ORDER BY country.Population DESC";
            if (limit > 0) {
                Country_query += " LIMIT " + limit;
            }
            // Execute the query and return the result
            return getCountryList(con, Country_query);
        }
    }

    public static List<City> getPopulatedCity(Connection con, String key, String value, int limit) {
        // Validate that limit must be a positive number
        if (limit < 0) {
            System.out.println("Limit must be a positive integer.");
            return new ArrayList<>();  // Return an empty list if limit is invalid
        }else{
            // Construct the base query
            String sql_query = "SELECT city.Name, country.Name AS CountryName, city.District, city.Population " +
                    "FROM city JOIN country ON city.CountryCode = country.Code ";
            // Check condition key and value are not add some extra text in query.
            if (key != null && value != null) {
                // If the key is "Name", "Continent", or "Region", filter using the country table
                if (key.equals("Name") || key.equals("Continent") || key.equals("Region")) {
                    sql_query += "WHERE country." + key + " = '" + value + "' ";
                    // If the key is "Name", "Continent", or "Region", filter using the country table
                } else if(limit ==0){
                    sql_query += "WHERE city." + key + " = '" + value + "' ";
                }
            }
            // Check connection for the limit
            if (limit > 0) {
                sql_query += "ORDER BY city.Population DESC LIMIT " + limit;
            }else if (limit == 0) {
                sql_query += "ORDER BY city.Population DESC";
            }
            return getCityList(con, sql_query);
        }
    }

    public static List<Population> getPopulation(Connection con, String key, String value) {
        if (con == null) {
            System.out.println("There is no database connection");
            return new ArrayList<>();  // Return an empty list if no connection
        } else {
            String pop_query = "SELECT ";
            if (key != null) {
                pop_query += key + " AS Name, SUM(country.Population) AS Total_Population, " +
                        "SUM(city.Population) AS Population_Live_In_Cities " +
                        "FROM country JOIN city ON country.Code = city.CountryCode ";
                if (value != null) {
                    pop_query += "WHERE " + key + " = '" + value + "' ";
                }
                pop_query += "GROUP BY " + key; // Group by after WHERE
            } else {
                pop_query += "'World' AS Name, SUM(country.Population) AS Total_Population, " +
                        "SUM(city.Population) AS Population_Live_In_Cities " +
                        "FROM country JOIN city ON country.Code = city.CountryCode ";
            }
            // Call the getPopulationList method and return the result
            return getPopulationList(con, pop_query);
        }
    }

    public static List<Capital> getPopulatedCapital(Connection con, String key, String value, int limit) {
        // Validate that limit must be a positive number
        if (limit < 0) {
            System.out.println("Limit must be a positive integer.");
            return new ArrayList<>();  // Return an empty list if limit is invalid
        } else {
            // Construct the base query
            String Capital_query = "SELECT city.Name AS capital, country.Name, country.Population " +
                    "FROM country JOIN city ON country.Capital = city.ID ";

            // If the key is "Name", "Continent", or "Region", filter using the country table
            if (key != null && value != null) {
                Capital_query += "WHERE country." + key + " = '" + value + "' ";
            }

            // Ensure a positive limit value for the query and add some necessary text
            if (limit > 0) {
                Capital_query += "ORDER BY country.Population DESC LIMIT " + limit;
            } else if (limit == 0) {
                Capital_query += "ORDER BY country.Population DESC"; // No limit if zero
            }

            return getCapitalList(con, Capital_query);
        }
    }

    public static List<Language> getLanguages(Connection con) {
        //Connection must not be null
        if (con == null) {
            System.out.println("No connection found.");
            return new ArrayList<>();
        } else {
            // SQL query for language report
            String que = "SELECT countrylanguage.Language, SUM(country.Population) AS Population " +
                    "FROM countrylanguage " +
                    "JOIN country ON countrylanguage.CountryCode = country.Code " +
                    "WHERE countrylanguage.Language IN ('Chinese', 'English', 'Hindi', 'Spanish', 'Arabic') " +
                    "GROUP BY countrylanguage.Language " +
                    "ORDER BY Population DESC";
            return getLanguageList(con, que);
        }
    }

    /**
     * Retrieves a list of capital cities from the database based on the provided SQL query.
     *
     * @param con           The Connection object to the database.
     * @param Capital_query The SQL query used to retrieve capital city data.
     * @return A List of Capital objects. If the connection is null or if no capitals are found, returns an empty list.
     */
    public static List<Capital> getCapitalList(Connection con, String Capital_query) {
        // Create an empty list to store capital cities
        List<Capital> capitals = new ArrayList<>();

        // Check if the database connection is null
        if (con == null) {
            System.out.println("No database connection.");
            return capitals;  // Return an empty list if there's no connection
        } else {
            // Proceed only if the query is not null
            if (Capital_query != null) {
                try {
                    // Create a statement object to execute the SQL query
                    Statement stmt = con.createStatement();
                    // Execute the query and store the result in ResultSet
                    ResultSet rset = stmt.executeQuery(Capital_query);

                    // Iterate through the ResultSet and retrieve data for each capital
                    while (rset.next()) {
                        // Retrieve the country name, population, and capital name from the result
                        String countryName = rset.getString("Name");
                        int population = rset.getInt("Population");
                        String capitalName = rset.getString("Capital");

                        // Create a new Capital object with the retrieved values
                        Capital capital = new Capital(countryName, population, capitalName);
                        // Add the Capital object to the list
                        capitals.add(capital);
                    }
                } catch (SQLException e) {
                    // Print an error message in case of an SQL exception
                    System.out.println("SQL Exception: " + e.getMessage());
                }
            }
        }
        // Return the list of capital cities
        return capitals;
    }

    /**
     * Retrieves a list of countries based on the provided query.
     *
     * @param con           The connection object to the database.
     * @param Country_query The SQL query string to retrieve country data.
     * @return A list of Country objects populated from the query results.
     */
    private static List<Country> getCountryList(Connection con, String Country_query) {
        // Initialize an empty list to store the countries
        List<Country> countries = new ArrayList<>();

        // Check if the query is null
        if (Country_query == null) {
            System.out.println("There is no value in variable");  // Log an error message if the query is missing
        } else {
            // Check if the database connection is valid
            if (con != null) {
                try {
                    // Create a statement and execute the query
                    Statement stmt = con.createStatement();
                    ResultSet rset = stmt.executeQuery(Country_query);

                    // Loop through the result set and add each country to the list
                    while (rset.next()) {
                        String countryCode = rset.getString("Code");
                        String countryName = rset.getString("Name");
                        String continent = rset.getString("Continent");
                        String region = rset.getString("Region");
                        int population = rset.getInt("Population");
                        String capital = rset.getString("Capital");

                        // Create a new Country object and add it to the list
                        Country country = new Country(countryCode, countryName, continent, region, population, capital);
                        countries.add(country);
                    }
                } catch (SQLException e) {
                    // Log an error message if there is an SQL exception
                    System.out.println("SQL Exception: " + e.getMessage());
                }
            } else {
                // Log an error message if the connection is null
                System.out.println("No database connection.");
                return countries;  // Return the empty list if no connection
            }
        }
        // Return the list of countries
        return countries;
    }

    /**
     * Retrieves a list of cities based on the provided SQL query.
     *
     * @param con       The connection object to the database.
     * @param sql_query The SQL query string to retrieve city data.
     * @return A list of City objects populated from the query results.
     */
    private static List<City> getCityList(Connection con, String sql_query) {
        // Initialize an empty list to store the cities
        List<City> cities = new ArrayList<>();

        // Check if the database connection is null
        if (con == null) {
            System.out.println("No database connection.");  // Log an error if there's no connection
            return cities;  // Return an empty list
        } else {
            // Check if the SQL query is provided
            if (sql_query != null) {
                try {
                    // Create a statement and execute the SQL query
                    Statement stmt = con.createStatement();
                    ResultSet rset = stmt.executeQuery(sql_query);

                    // Populate the city list (from largest to smallest population)
                    while (rset.next()) {
                        String name = rset.getString("Name");
                        String countryName = rset.getString("CountryName"); // Country name from the joined country table
                        String district = rset.getString("District");
                        int population = rset.getInt("Population");

                        // Create a new City object and add it to the list
                        City city = new City(name, countryName, district, population);
                        cities.add(city);
                    }
                } catch (SQLException e) {
                    // Log an error message if there is an SQL exception
                    System.out.println(e.getMessage());
                }
            } else {
                // Log an error if the SQL query is null
                System.out.println("No SQL query.");
            }
        }
        // Return the list of cities
        return cities;
    }

    /**
     * Retrieves a list of languages and their populations from the database.
     *
     * @param con The connection object to the database.
     * @param que The SQL query string to retrieve language data.
     * @return A list of Language objects populated from the query results.
     */
    public static List<Language> getLanguageList(Connection con, String que) {
        List<Language> languages = new ArrayList<>(); // Initialize an empty list to store languages
        if (con == null) { // Check if the database connection is null
            System.out.println("No connection found."); // Log an error if there's no connection
        } else {
            if (que != null) { // Check if the SQL query is provided
                try {
                    Statement stmt = con.createStatement(); // Create a statement to execute the SQL query
                    ResultSet rset = stmt.executeQuery(que); // Execute the query and store the results

                    // Loop through the result set and create Language objects
                    while (rset.next()) {
                        String language = rset.getString("Language"); // Get the language name
                        int population = rset.getInt("Population"); // Get the population for that language

                        Language lang = new Language(language, population); // Create a Language object
                        languages.add(lang); // Add the Language object to the list
                    }
                } catch (SQLException e) { // Catch any SQL exceptions
                    System.out.println("SQL Error: " + e.getMessage()); // Log the error message
                }
            } else {
                System.out.println("No SQL query."); // Log an error if the SQL query is null
            }
        }
        return languages; // Return the list of languages
    }

    /**
     * Retrieves a list of populations from the database based on the provided SQL query.
     *
     * @param con The connection object to the database.
     * @param pop_query The SQL query string to retrieve population data.
     * @return A list of Population objects populated from the query results.
     */
    public static List<Population> getPopulationList(Connection con, String pop_query) {
        List<Population> populations = new ArrayList<>(); // Initialize an empty list to store populations
        if (con == null) { // Check if the database connection is null
            System.out.println("There is no database connection"); // Log an error message
            return populations; // Return an empty list
        } else {
            if (pop_query != null) { // Check if the SQL query is provided
                try {
                    Statement stmt = con.createStatement(); // Create a statement to execute the SQL query
                    ResultSet rset = stmt.executeQuery(pop_query); // Execute the query and store the results
                    // Loop through the result set and create Population objects
                    while (rset.next()) {
                        String name = rset.getString("Name"); // Get the country or region name
                        long totalPopulation = rset.getLong("Total_Population"); // Get total population
                        long populationInCities = rset.getLong("Population_Live_In_Cities"); // Get population living in cities
                        long populationNotInCities = totalPopulation - populationInCities; // Calculate population not living in cities

                        // Calculate percentages correctly using double to avoid integer division
                        double percentageInCities = totalPopulation > 0 ? ((double) populationInCities / totalPopulation) * 100 : 0;
                        double percentageNotInCities = totalPopulation > 0 ? ((double) populationNotInCities / totalPopulation) * 100 : 0;

                        Population population = new Population(name, totalPopulation, populationNotInCities, populationInCities, percentageInCities, percentageNotInCities); // Create a Population object
                        populations.add(population); // Add the Population object to the list
                    }
                } catch (SQLException e) { // Catch any SQL exceptions
                    System.out.println("SQL Error: " + e.getMessage()); // Log the error message
                }
            } else {
                System.out.println("No SQL query."); // Log an error if the SQL query is null
            }
            return populations; // Return the list of populations
        }
    }


    /**
     * Prints a list of capitals in a table format and writes to a markdown file.
     *
     * @param capitals The list of Capital objects to be printed.
     * @param header   A string to be printed as the header of the table.
     * @param report   The name of the markdown file to be created.
     */
    public static void printCapitals(List<Capital> capitals, String header, String report) {
        // Check if the capitals list is null or empty
        if (capitals == null || capitals.isEmpty()) {
            System.out.println("No countries found.");
        } else {
            // Console Output
            System.out.println("\n\n######## " + header + " ########");
            System.out.println("+-----------------------------------+-----------------------------------------+------------+");
            System.out.printf("|%-33s | %-39s | %-10s |\n", "Name", "Country Name", "Population");
            System.out.println("+-----------------------------------+-----------------------------------------+------------+");

            for (Capital capital : capitals) {
                System.out.printf("|%-33s | %-39s | %-10s |\n", capital.getCapital(), capital.getName(), String.format("%,d", capital.getPopulation()));
            }

            System.out.println("+-----------------------------------+-----------------------------------------+------------+");
            System.out.println(capitals.size() + " countries found.");

            // File Output to Markdown
            try {
                new File("./reports/").mkdir();
                BufferedWriter writer = new BufferedWriter(new FileWriter(new File("./reports/" + report + ".md")));

                writer.write("### " + header + "\n\n");
                writer.write("| Capital | Country Name | Population |\n");
                writer.write("| --- | --- | --- |\n");

                for (Capital capital : capitals) {
                    writer.write("| " + capital.getCapital() + " | " + capital.getName() + " | " + String.format("%,d", capital.getPopulation()) + " |\n");
                }

                writer.write("\n" + capitals.size() + " capitals found.\n");
                writer.close();
                System.out.println("Report written to ./reports/" + report + ".md");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Prints a list of countries in a table format and writes to a markdown file.
     *
     * @param countries The list of Country objects to be printed.
     * @param header    A string to be printed as the header of the table.
     * @param report    The name of the markdown file to be created.
     */
    public static void printCountries(List<Country> countries, String header, String report) {
        // Check if the countries list is null or empty
        if (countries == null || countries.isEmpty()) {
            System.out.println("No countries found.");
        } else {
            // Console Output
            System.out.println("\n\n######## " + header + " ########");
            System.out.println("+--------------+-----------------------------------------+---------------+-----------------------------+------------+-----------------------------------+");
            System.out.printf("| %-12s | %-39s | %-13s | %-27s | %-10s | %-33s |\n",
                    "Country Code", "Country Name", "Continent", "Region", "Population", "Capital");
            System.out.println("+--------------+-----------------------------------------+---------------+-----------------------------+------------+-----------------------------------+");

            for (Country country : countries) {
                System.out.printf("| %-12s | %-39s | %-13s | %-27s | %-10s | %-33s |\n",
                        country.getCountryCode(), country.getName(), country.getContinent(),
                        country.getRegion(), String.format("%,d", country.getPopulation()), country.getCapital());
            }

            System.out.println("+--------------+-----------------------------------------+---------------+-----------------------------+------------+-----------------------------------+");
            System.out.println(countries.size() + " countries found.");

            // File Output to Markdown
            try {
                new File("./reports/").mkdir();
                BufferedWriter writer = new BufferedWriter(new FileWriter(new File("./reports/" + report + ".md")));

                writer.write("### " + header + "\n\n");
                writer.write("| Country Code | Country Name | Continent | Region | Population | Capital |\n");
                writer.write("| --- | --- | --- | --- | --- | --- |\n");

                for (Country country : countries) {
                    writer.write("| " + country.getCountryCode() + " | " + country.getName() + " | "
                            + country.getContinent() + " | " + country.getRegion() + " | "
                            + String.format("%,d", country.getPopulation()) + " | " + country.getCapital() + " |\n");
                }

                writer.write("\n" + countries.size() + " countries found.\n");
                writer.close();
                System.out.println("Report written to ./reports/" + report + ".md");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Prints a list of cities in a table format and writes to a markdown file.
     *
     * @param cities  The list of City objects to be printed.
     * @param header  A string to be printed as the header of the table.
     * @param report  The name of the markdown file to be created.
     */
    public static void printCities(List<City> cities, String header, String report) {
        // Check if the cities list is null or empty
        if (cities == null || cities.isEmpty()) {
            System.out.println("No cities found.");
        } else {
            // Console Output
            System.out.println("\n\n######## " + header + " ########");
            System.out.println("+------------------------------------+-----------------------------------------+-----------------------------+------------+");
            System.out.printf("| %-34s | %-39s | %-27s | %-10s |\n", "City Name", "Country Name", "District", "Population");
            System.out.println("+------------------------------------+-----------------------------------------+-----------------------------+------------+");

            for (City city : cities) {
                System.out.printf("| %-34s | %-39s | %-27s | %-10s |\n", city.getName(), city.getCountryName(), city.getDistrict(), String.format("%,d", city.getPopulation()));
            }

            System.out.println("+------------------------------------+-----------------------------------------+-----------------------------+------------+");
            System.out.println(cities.size() + " cities found.");

            // File Output to Markdown
            try {
                new File("./reports/").mkdir();
                BufferedWriter writer = new BufferedWriter(new FileWriter(new File("./reports/" + report + ".md")));

                writer.write("### " + header + "\n\n");
                writer.write("| City Name | Country Name | District | Population |\n");
                writer.write("| --- | --- | --- | --- |\n");

                for (City city : cities) {
                    writer.write("| " + city.getName() + " | " + city.getCountryName() + " | "
                            + city.getDistrict() + " | " + String.format("%,d", city.getPopulation()) + " |\n");
                }

                writer.write("\n" + cities.size() + " cities found.\n");
                writer.close();
                System.out.println("Report written to ./reports/" + report + ".md");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Prints a list of population details in a table format and writes to a markdown file.
     *
     * @param populations  The list of Population objects to be printed.
     * @param header      A string to be printed as the header of the table.
     * @param report      The name of the markdown file to be created.
     */
    public static void printPopulationEach(List<Population> populations, String header, String report) {
        // Check if the list of populations is null or empty
        if (populations == null || populations.isEmpty()) {
            System.out.println("No result found in variable"); // Print a message if no population data is available
        } else {
            // Print the header for the console output
            System.out.println("\n\n######## " + header + " ########"); // Print the main header
            System.out.println("+-----------------------------------+------------------+---------------------------+-------------------------------+"); // Print the table separator
            // Print the column titles for the console output
            System.out.printf("| %-33s | %-15s | %-25s | %-25s |\n",
                    "Name",
                    "Total Population",
                    "Population Live In Cities",
                    "Population Not Live In Cities");
            System.out.println("+-----------------------------------+------------------+---------------------------+-------------------------------+"); // Print the table separator

            // Iterate over the population list and print each population's details
            for (Population population : populations) {
                System.out.printf("| %-33s | %,16d | %,15d [ %.2f%% ] | %,18d [ %.2f%% ] |\n",
                        population.getName() != null ? population.getName() : "World", // Use "World" if name is null
                        population.getTotalPopulation(),
                        population.getPopulationInCities(),
                        population.getPercentageInCities(),
                        population.getPopulationNotInCities(),
                        population.getPercentageNotInCities());
            }
            // Print the footer of the console output
            System.out.println("+-----------------------------------+------------------+---------------------------+-------------------------------+"); // Print the table separator
            System.out.println(populations.size() + " RESULTS FOUND IN THIS REPORT"); // Print the total number of results found

            // File output to a Markdown report
            try {
                // Create the reports directory if it doesn't exist
                new File("./reports/").mkdir();
                // Create a BufferedWriter to write the report to a Markdown file
                BufferedWriter writer = new BufferedWriter(new FileWriter(new File("./reports/" + report + ".md")));

                // Write the header for the Markdown report
                writer.write("### " + header + "\n\n");
                writer.write("| Name | Total Population | Population Live In Cities | Population Not Live In Cities |\n");
                writer.write("| --- | --- | --- | --- |\n");

                // Write each population's details to the Markdown report
                for (Population population : populations) {
                    writer.write("| " + (population.getName() != null ? population.getName() : "World") + " | "
                            + String.format("%,d", population.getTotalPopulation()) + " | "
                            + String.format("%,d [ %.2f%% ]", population.getPopulationInCities(), population.getPercentageInCities()) + " | "
                            + String.format("%,d [ %.2f%% ]", population.getPopulationNotInCities(), population.getPercentageNotInCities()) + " |\n");
                }
                // Write the total results found at the end of the report
                writer.write("\n" + populations.size() + " RESULTS FOUND IN THIS REPORT\n");
                writer.close(); // Close the writer
                System.out.println("Report written to ./reports/" + report + ".md"); // Print a message indicating where the report is saved
            } catch (IOException e) {
                e.printStackTrace(); // Print the stack trace if there's an error writing the report
            }
        }
    }



    /**
     * Prints a table of languages and their populations in a formatted manner,
     * and writes the data to a markdown file.
     *
     * @param languages The list of Language objects to be printed.
     * @param report    The name of the markdown file to be created.
     */
    public static void printLanguageTable(List<Language> languages, String report) {
        // Check if the list of languages is null or empty
        if (languages == null || languages.isEmpty()) {
            System.out.println("No results found."); // Print message if no results
        } else {
            // Console Output: Print the table header
            System.out.println("+-------------------+------------------+");
            System.out.printf("| %-17s | %-16s |\n", "Language", "Population");
            System.out.println("+-------------------+------------------+");

            // Iterate over the list of languages and print each language's details
            for (Language lang : languages) {
                System.out.printf("| %-17s | %,16d |\n", lang.getLanguage(), lang.getPopulation());
            }

            // Print the footer of the console output
            System.out.println("+-------------------+------------------+");
            System.out.println(languages.size() + " results found."); // Print total results found

            // File Output to Markdown: Create report file
            try {
                new File("./reports/").mkdir(); // Create the reports directory if it doesn't exist
                BufferedWriter writer = new BufferedWriter(new FileWriter(new File("./reports/" + report + ".md")));

                // Write the header for the Markdown report
                writer.write("### Language Population Report\n\n");
                writer.write("| Language | Population |\n");
                writer.write("| --- | --- |\n");

                // Write each language's details to the Markdown report
                for (Language lang : languages) {
                    writer.write("| " + lang.getLanguage() + " | " + String.format("%,d", lang.getPopulation()) + " |\n");
                }

                // Write the total results found at the end of the report
                writer.write("\n" + languages.size() + " results found.\n");
                writer.close(); // Close the writer
                System.out.println("Report written to ./reports/" + report + ".md"); // Print a message indicating where the report is saved
            } catch (IOException e) {
                e.printStackTrace(); // Print the stack trace if there's an error writing the report
            }
        }
    }




    // Function to display various populated countries, cities, and capitals in table format
    public void Table_display() {
        ////Most populated countries by world, contient, region [ DESC]
        List<Country> countryByWorld = getPopulatedCountries(con, null, null, 0); // Fetch top 10 populated countries
        printCountries(countryByWorld, "---------------------Most populated countries [World]---------------------", "Most Populated Country [World]");
        List<Country> countryByContinent = getPopulatedCountries(con, "Continent", "Europe", 0); // Fetch top 10 populated countries
        printCountries(countryByContinent, "---------------------Most populated countries [Continent] [Europe]---------------------", "Most Populated Country [Continent] [Europe]" );
        List<Country> countryByRegion = getPopulatedCountries(con, "Region", "Southern and Central Asia", 0); // Fetch top 10 populated countries
        printCountries(countryByRegion, "---------------------World most populated countries [Region] [Southern and Central Asia]---------------------", "Most Populated Country [Region] [Southern and Central Asia]" );

        ////Top N populated countries by world, continent, region
        List<Country> Top_10_ByWorld = getPopulatedCountries(con, null, null, 10); // Fetch top 10 populated countries
        printCountries(Top_10_ByWorld, "---------------------Top 10 most populated countries [World]---------------------","Top 10 Most Populated Countries [World]");
        List<Country> Top_10_ByContinent = getPopulatedCountries(con, "Continent", "North America", 10); // Fetch top 10 populated countries
        printCountries(Top_10_ByContinent, "---------------------Top 10 most populated countries [Continent][North America ]---------------------","Top 10 Most Populated Countries [Continent]");
        List<Country> Top_10_ByRegion = getPopulatedCountries(con, "Region", "Caribbean", 10); // Fetch top 10 populated countries
        printCountries(Top_10_ByRegion, "---------------------Top 10 most populated countries [Region][Caribbean]---------------------","Top 10 Most Populated Countries [Region]");

        //// Most populated cities by world, continent, region, country, district [DESC]
        List<City> cityByWorld = getPopulatedCity(con, null, null, 0);
        printCities(cityByWorld, "---------------------Most populated cities [World]---------------------", "Most Populated City [World]");
        List<City> cityByContinent = getPopulatedCity(con, "Continent", "Africa", 0);
        printCities(cityByContinent, "---------------------Most populated cities [Continent][Africa]---------------------", "Most Populated City [Continent]");
        List<City> cityByRegion = getPopulatedCity(con, "Region", "Middle East", 0);
        printCities(cityByRegion, "---------------------Most populated cities [Region][Middle East]---------------------", "Most Populated City [Region]");
        List<City> cityByCountry = getPopulatedCity(con, "Name","Russian Federation",0);
        printCities(cityByCountry, "---------------------Most populated cities [Country][Russia]---------------------", "Most Populated City [Country]");
        List<City> cityByDistrict = getPopulatedCity(con, "District","Gelderland",0);
        printCities(cityByDistrict, "---------------------Most populated cities [District][Gelderland]---------------------","Most Populated City [District]");

        ////Top N populated cities by world, continent, region, country, district
        List<City> TopByWorld = getPopulatedCity(con, null,null,10);
        printCities(TopByWorld, "---------------------Top 10 populated cities [World]---------------------", "Top 10 Most Populated City [World]");
        List<City> TopByContinent = getPopulatedCity(con, "Continent","Asia",10);
        printCities(TopByContinent, "---------------------Top 10 populated cities [Continent][Asia]---------------------", "Top 10 Most Populated City [Continent]");
        List<City> TopByRegion = getPopulatedCity(con, "Region","British Islands",10);
        printCities(TopByRegion, "---------------------Top 10 populated cities [Region][British Islands]---------------------", "Top 10 Most Populated City [Region]");
        List<City> TopByCountry = getPopulatedCity(con, "Name","Myanmar",10);
        printCities(TopByCountry, "---------------------Top 10 populated cities [Country][Myanmar]---------------------", "Top 10 Most Populated City [Country]");
        List<City> TopByDistrict = getPopulatedCity(con, "District","México",10);
        printCities(TopByDistrict, "---------------------Top 10 populated cities [District][México]---------------------", "Top 10 Most Populated City [District]");

        //// Most populated capital cities by world, continent, region [DESC]
        List<Capital>CapitalByWorld = getPopulatedCapital(con, null,null,0);
        printCapitals(CapitalByWorld, "---------------------Most populated Capital [World]---------------------", "Most Populated Capital [World]");
        List<Capital>CapitalByContinent = getPopulatedCapital(con, "Continent","South America",0);
        printCapitals(CapitalByContinent, "---------------------Most populated Capital [Continent][]---------------------", "Most Populated Capital [Continent]");
        List<Capital>CapitalByRegion = getPopulatedCapital(con, "Region","Polynesia",0);
        printCapitals(CapitalByRegion, "---------------------Most populated Capital [Region][Polynesia]---------------------", "Most Populated Capital [Region]");

        ////Top N populated capital cities by world, continent, region
        List<Capital>TopCapitalByWorld = getPopulatedCapital(con, null,null,5);
        printCapitals(TopCapitalByWorld, "---------------------Top 5 populated cities [Continent][]---------------------", "Top 5 Most Populated Capital [World]");
        List<Capital>TopCapitalByContinent = getPopulatedCapital(con, "Continent","North America",5);
        printCapitals(TopCapitalByContinent, "---------------------Top 5 populated Capital [Continent][]---------------------", "Top 5 Most Populated Capital [Continent]");
        List<Capital>TopCapitalByRegion = getPopulatedCapital(con, "Region","Caribbean",5);
        printCapitals(TopCapitalByRegion, "---------------------Top 5 populated Capital [Region][Caribbean]---------------------", "Top 5 Most Populated Capital [Region]");

        List<Population> Population_of_Continent = getPopulation(con, "country.Continent", null);
        printPopulationEach(Population_of_Continent, "---------------------Population Report for Each Continent---------------------", "Each Continent Population Report");
        List<Population> Population_of_Region = getPopulation(con, "country.Region", null);
        printPopulationEach(Population_of_Region, "---------------------Population Report for Each Region---------------------","Each Region Population Report");
        List<Population> Population_of_Country = getPopulation(con, "country.Name", null);
        printPopulationEach(Population_of_Country, "---------------------Population Report for Each Country---------------------","Each Country Population Report");

        List<Population> World_Population = getPopulation(con, null, null);
        printPopulationEach(World_Population, "---------------------Population Of World---------------------","World Population");
        List<Population> Continent_Population = getPopulation(con, "country.Continent", "North America");
        printPopulationEach(Continent_Population, "---------------------Population Report of Continent ---------------------","Continent Population ");
        List<Population> Country_Population = getPopulation(con, "country.Name", "Albania");
        printPopulationEach(Country_Population, "---------------------Population Report for Country---------------------","Country Population");
        List<Population> City_Population = getPopulation(con, "city.Name", "Eindhoven");
        printPopulationEach(City_Population, "---------------------Population Report for City---------------------","City Population");
        List<Population> District_Population = getPopulation(con, "city.District", "Zuid-Holland");
        printPopulationEach(District_Population, "---------------------Population Report for District---------------------","District Population");

        List<Language> languages = getLanguages(con);
        printLanguageTable(languages, "Language Report");

        List<Population> Region_Population = getPopulation(con, "country.Region", "Southern Europe");
        printPopulationEach(Region_Population, "---------------------Population Report for Region---------------------"," Region Population");
    }

// Need to run world.sql database before running App
    public static void main(String[] args) {
        // Create new Application instance
        App app = new App();

        // Connect to database
        if(args.length < 1){
            app.connect("localhost:33060", 0); ////Connection to the database with localhost port number 33060 and delay time zero
        }else{
            app.connect("db:3306", 10000);
        }

        // Set the limit to a positive number to retrieve results
        app.Table_display();
        app.disconnect();
    }
}
