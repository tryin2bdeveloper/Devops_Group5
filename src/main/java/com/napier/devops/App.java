package com.napier.devops;

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
     * Prints a list of capitals in a table format.
     *
     * @param capitals The list of Capital objects to be printed.
     * @param header   A string to be printed as the header of the table.
     */
    public static void printCapitals(List<Capital> capitals, String header) {
        // Check if the capitals list is null
        if (capitals == null) {
            System.out.println("No countries found.");  // Output message if no capitals are found
        } else {
            // Print the header and table format
            System.out.println("\n\n######## " + header + " ########");
            System.out.println("+-----------------------------------+-----------------------------------------+------------+");
            System.out.printf("|%-33s | %-39s | %-10s |\n", "Name", "Country Name", "Population");
            System.out.println("+-----------------------------------+-----------------------------------------+------------+");

            // Loop through each capital and print its details
            for (Capital capital : capitals) {
                System.out.printf("|%-33s | %-39s | %-10s |\n", capital.getCapital(), capital.getName(), String.format("%,d", capital.getPopulation()));
            }

            // Print the footer and count of capitals
            System.out.println("+-----------------------------------+-----------------------------------------+------------+");
            System.out.println(capitals.size() + " countries found.");
        }
    }

    /**
     * Prints a list of countries in a table format.
     *
     * @param countries The list of Country objects to be printed.
     * @param header    A string to be printed as the header of the table.
     */
    public static void printCountries(List<Country> countries, String header) {
        // Check if the countries list is null
        if (countries == null) {
            System.out.println("No countries found.");  // Output message if no countries are found
        } else {
            // Print the header and table format
            System.out.println("\n\n######## " + header + " ########");
            System.out.println("+--------------+-----------------------------------------+---------------+-----------------------------+------------+-----------------------------------+");
            System.out.printf("| %-12s | %-39s | %-13s | %-27s | %-10s | %-33s |\n",
                    "Country Code", "Country Name", "Continent", "Region", "Population", "Capital");
            System.out.println("+--------------+-----------------------------------------+---------------+-----------------------------+------------+-----------------------------------+");

            // Loop through each country and print its details
            for (Country country : countries) {
                System.out.printf("| %-12s | %-39s | %-13s | %-27s | %-10s | %-33s |\n",
                        country.getCountryCode(), country.getName(), country.getContinent(),
                        country.getRegion(), String.format("%,d", country.getPopulation()), country.getCapital());
            }

            // Print the footer and count of countries
            System.out.println("+--------------+-----------------------------------------+---------------+-----------------------------+------------+-----------------------------------+");
            System.out.println(countries.size() + " countries found.");
        }
    }

    /**
     * Prints a list of cities in a table format.
     *
     * @param cities  The list of City objects to be printed.
     * @param header  A string to be printed as the header of the table.
     */
    public static void printCities(List<City> cities, String header) {
        // Check if the cities list is null
        if (cities == null) {
            System.out.println("No countries found.");  // Output message if no cities are found
        } else {
            // Print the header and table format
            System.out.println("\n\n######## " + header + " ########");
            System.out.println("+------------------------------------+-----------------------------------------+-----------------------------+------------+");
            System.out.printf("| %-34s | %-39s | %-27s | %-10s |\n", "City Name", "Country Name", "District", "Population");
            System.out.println("+------------------------------------+-----------------------------------------+-----------------------------+------------+");

            // Loop through each city and print its details
            for (City city : cities) {
                System.out.printf("| %-34s | %-39s | %-27s | %-10s |\n", city.getName(), city.getCountryName(), city.getDistrict(), String.format("%,d", city.getPopulation()));
            }

            // Print the footer and count of cities
            System.out.println("+------------------------------------+-----------------------------------------+-----------------------------+------------+");
            System.out.println(cities.size() + " countries found.");
        }
    }

    public static List<Population> getPopulation(Connection con, String key) {
        if (con == null) {
            System.out.println("There is no database connection");
            return new ArrayList<>();  // Return an empty list if no connection
        } else {
            String pop_query = "SELECT " + key + " AS Name, SUM(country.Population) AS Total_Population, SUM(city.Population) AS Population_Live_In_Cities " +
                    "FROM country JOIN city ON country.Code = city.CountryCode ";
            if (key != null) {
                pop_query += "GROUP BY country." + key;
            }
            // Call the getPopulationList method and return the result
            return getPopulationList(con, pop_query);
        }
    }

    public static List<Population> getPopulationList(Connection con, String pop_query) {
        List<Population> populations = new ArrayList<>();
        if (con == null) {
            System.out.println("There is no database connection");
            return populations;
        } else {
            if (pop_query != null) {
                try {
                    Statement stmt = con.createStatement();
                    ResultSet rset = stmt.executeQuery(pop_query);
                    while (rset.next()) {
                        String name = rset.getString("Name");
                        long totalPopulation = rset.getLong("Total_Population"); // Use long
                        long populationInCities = rset.getLong("Population_Live_In_Cities"); // Use long
                        long populationNotInCities = totalPopulation - populationInCities;

                        // Calculate percentages correctly using double to avoid integer division
                        double percentageInCities = totalPopulation > 0 ? ((double) populationInCities / totalPopulation) * 100 : 0;
                        double percentageNotInCities = totalPopulation > 0 ? ((double) populationNotInCities / totalPopulation) * 100 : 0;

                        Population population = new Population(name, totalPopulation, populationNotInCities, populationInCities, percentageInCities, percentageNotInCities);
                        populations.add(population);
                    }
                } catch (SQLException e) {
                    // Log an error message if there is an SQL exception
                    System.out.println("SQL Error: " + e.getMessage());
                }
            } else {
                System.out.println("No SQL query.");
            }
            return populations;
        }
    }

    public static void printPopulationEach(List<Population> populations, String header) {
        if (populations == null || populations.isEmpty()) {
            System.out.println("No result found in variable");
        } else {
            System.out.println("\n\n######## " + header + " ########");
            System.out.println("+-----------------------------------+------------------+---------------------------+-------------------------------+");
            System.out.printf("| %-33s | %-15s | %-25s | %-25s |\n",
                    "Name",
                    "Total Population",
                    "Population Live In Cities",
                    "Population Not Live In Cities");
            System.out.println("+-----------------------------------+------------------+---------------------------+-------------------------------+");

            for (Population population : populations) {
                System.out.printf("| %-33s | %,16d | %,15d [ %.2f%% ] | %,18d [ %.2f%% ] |\n",
                        population.getName(),
                        population.getTotalPopulation(),          // Population formatted with commas
                        population.getPopulationInCities(),       // Population in cities formatted with commas
                        population.getPercentageInCities(),       // Percentage in cities with '%'
                        population.getPopulationNotInCities(),    // Population not in cities formatted with commas
                        population.getPercentageNotInCities());   // Percentage not in cities with '%'
            }

            System.out.println("+-----------------------------------+------------------+---------------------------+-------------------------------+");
            System.out.println(populations.size() + " RESULTS FOUND IN THIS REPORT");
        }
    }


    // Function to display various populated countries, cities, and capitals in table format
    public void Table_display() {

        ////Most populated countries by world, contient, region [ DESC]
        List<Country> countryByWorld = getPopulatedCountries(con, null, null, 0); // Fetch top 10 populated countries
        printCountries(countryByWorld, "---------------------Most populated countries [World]---------------------");
        List<Country> countryByContinent = getPopulatedCountries(con, "Continent", "Europe", 0); // Fetch top 10 populated countries
        printCountries(countryByContinent, "---------------------Most populated countries [Continent] [Europe]---------------------");
        List<Country> countryByRegion = getPopulatedCountries(con, "Region", "Southern and Central Asia", 0); // Fetch top 10 populated countries
        printCountries(countryByRegion, "---------------------World most populated countries [Region] [Southern and Central Asia]---------------------");

        ////Top N populated countries by world, continent, region
        List<Country> Top_10_ByWorld = getPopulatedCountries(con, null, null, 10); // Fetch top 10 populated countries
        printCountries(Top_10_ByWorld, "---------------------Top 10 most populated countries [World]---------------------");
        List<Country> Top_10_ByContinent = getPopulatedCountries(con, "Continent", "North America", 10); // Fetch top 10 populated countries
        printCountries(Top_10_ByContinent, "---------------------Top 10 most populated countries [Continent][North America ]---------------------");
        List<Country> Top_10_ByRegion = getPopulatedCountries(con, "Region", "Caribbean", 10); // Fetch top 10 populated countries
        printCountries(Top_10_ByRegion, "---------------------Top 10 most populated countries [Region][Caribbean]---------------------");

        //// Most populated cities by world, continent, region, country, district [DESC]
        List<City> cityByWorld = getPopulatedCity(con, null, null, 0);
        printCities(cityByWorld, "---------------------Most populated cities [World]---------------------");
        List<City> cityByContinent = getPopulatedCity(con, "Continent", "Africa", 0);
        printCities(cityByContinent, "---------------------Most populated cities [Continent][Africa]---------------------");
        List<City> cityByRegion = getPopulatedCity(con, "Region", "Middle East", 0);
        printCities(cityByRegion, "---------------------Most populated cities [Region][Middle East]---------------------");
        List<City> cityByCountry = getPopulatedCity(con, "Name","Russian Federation",0);
        printCities(cityByCountry, "---------------------Most populated cities [Country][Russia]---------------------");
        List<City> cityByDistrict = getPopulatedCity(con, "District","Gelderland",0);
        printCities(cityByDistrict, "---------------------Most populated cities [District][Gelderland]---------------------");

        ////Top N populated cities by world, continent, region, country, district
        List<City> TopByWorld = getPopulatedCity(con, null,null,10);
        printCities(TopByWorld, "---------------------Top 10 populated cities [World]---------------------");
        List<City> TopByContinent = getPopulatedCity(con, "Continent","Asia",10);
        printCities(TopByContinent, "---------------------Top 10 populated cities [Continent][Asia]---------------------");
        List<City> TopByRegion = getPopulatedCity(con, "Region","British Islands",10);
        printCities(TopByRegion, "---------------------Top 10 populated cities [Region][British Islands]---------------------");
        List<City> TopByCountry = getPopulatedCity(con, "Name","Myanmar",10);
        printCities(TopByCountry, "---------------------Top 10 populated cities [Country][Myanmar]---------------------");
        List<City> TopByDistrict = getPopulatedCity(con, "District","México",10);
        printCities(TopByDistrict, "---------------------Top 10 populated cities [District][México]---------------------");

        //// Most populated capital cities by world, continent, region [DESC]
        List<Capital>CapitalByWorld = getPopulatedCapital(con, null,null,0);
        printCapitals(CapitalByWorld, "---------------------Most populated Capital [World]---------------------");
        List<Capital>CapitalByContinent = getPopulatedCapital(con, "Continent","South America",0);
        printCapitals(CapitalByContinent, "---------------------Most populated Capital [Continent][]---------------------");
        List<Capital>CapitalByRegion = getPopulatedCapital(con, "Region","Polynesia",0);
        printCapitals(CapitalByRegion, "---------------------Most populated Capital [Region][Polynesia]---------------------");

        ////Top N populated capital cities by world, continent, region
        List<Capital>TopCapitalByWorld = getPopulatedCapital(con, null,null,5);
        printCapitals(TopCapitalByWorld, "---------------------Top 5 populated cities [Continent][]---------------------");
        List<Capital>TopCapitalByContinent = getPopulatedCapital(con, "Continent","North America",5);
        printCapitals(TopCapitalByContinent, "---------------------Top 5 populated Capital [Continent][]---------------------");
        List<Capital>TopCapitalByRegion = getPopulatedCapital(con, "Region","Caribbean",5);
        printCapitals(TopCapitalByRegion, "---------------------Top 5 populated Capital [Region][Caribbean]---------------------");

        List<Population> Population_of_Continent = getPopulation(con, "Continent");
        printPopulationEach(Population_of_Continent, "---------------------Population Report for Each Continent---------------------");
        List<Population> Population_of_Cont = getPopulation(con, "Continent");
        printPopulationEach(Population_of_Cont, "---------------------Population Report for Each Continent---------------------");
        List<Population> Population_of_Continet = getPopulation(con, "Continent");
        printPopulationEach(Population_of_Continet, "---------------------Population Report for Each Continent---------------------");
        List<Population> Population_of_Contint = getPopulation(con, "Continent");
        printPopulationEach(Population_of_Contint, "---------------------Population Report for Each Continent---------------------");
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
