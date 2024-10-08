package com.napier.devops;

import com.mysql.cj.xdevapi.Table;

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
    public void connect() {
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
                Thread.sleep(30000);
                // Connect to database
                con = DriverManager.getConnection("jdbc:mysql://db:3306/world?useSSL=false&allowPublicKeyRetrieval=true", "root", "group-5");
                System.out.println("Successfully connected");
                break;
            } catch (SQLException sqle) {
                System.out.println("Failed to connect to database attempt " + Integer.toString(i));
                System.out.println(sqle.getMessage());
            } catch (InterruptedException ie) {
                System.out.println("Thread interrupted? Should not happen.");
            }
        }
    }

    /**
     * Disconnect from the MySQL database.
     */
    public void disconnect() {
        if (con != null) {
            try {
                // Close connection
                con.close();
            } catch (Exception e) {
                System.out.println("Error closing connection to database");
            }
        }
    }

    public static List<Country> getPopulatedCountries(Connection con, String key, String value, int limit) {
        String Country_query = "SELECT country.Code, country.Name, country.Continent, country.Region, country.Population, city.Name AS Capital " +
                "FROM country JOIN city ON country.Capital = city.ID ";

        if (key != null && value != null) {
            Country_query += "WHERE country." + key + " = '" + value + "' ";
        }

        // Ensure a positive limit value for the query
        if (limit > 0) {
            Country_query += "ORDER BY country.Population DESC LIMIT " + limit;

        } else {
            Country_query += "ORDER BY country.Population DESC"; // No limit if zero or negative
        }

        return getCountryList(con, Country_query);
    }

    public static List<City> getPopulatedCity(Connection con, String key, String value, int limit) {
        String sql_query = "SELECT city.Name, country.Name AS CountryName, city.District, city.Population " +
                "FROM city JOIN country ON city.CountryCode = country.Code ";

        if (key != null && value != null) {
            if (key.equals("Name") || key.equals("Continent") || key.equals("Region")) {
                sql_query += "WHERE country." + key + " = '" + value + "' ";
            } else {
                sql_query += "WHERE city." + key + " = '" + value + "' ";
            }
        }


        if (limit > 0) {
            sql_query += "ORDER BY city.Population DESC LIMIT " + limit;
        }else {
          sql_query += "ORDER BY city.Population DESC";
        }
        return getCityList(con, sql_query);
    }

    // Helper function to execute the query and return a list of countries
    private static List<Country> getCountryList(Connection con, String Country_query) {
        List<Country> countries = new ArrayList<>();
        try {
            Statement stmt = con.createStatement();
            ResultSet rset = stmt.executeQuery(Country_query);

            // Populate country list
            while (rset.next()) {
                String countryCode = rset.getString("Code");
                String countryName = rset.getString("Name");
                String continent = rset.getString("Continent");
                String region = rset.getString("Region");
                int population = rset.getInt("Population");
                String capital = rset.getString("Capital");

                Country country = new Country(countryCode, countryName, continent, region, population, capital);
                countries.add(country);
            }

        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        }
        return countries;
    }

    // Function to print countries in a table format
    public static void printCountries(List<Country> countries, String header) {
        System.out.println("\n\n######## " + header + " ########");
        System.out.println("+--------------+-----------------------------------------+---------------+-----------------------------+------------+-----------------------------------+");
        System.out.printf("| %-12s | %-39s | %-13s | %-27s | %-10s | %-33s |\n",
                "Country Code", "Country Name", "Continent", "Region", "Population", "Capital");
        System.out.println("+--------------+-----------------------------------------+---------------+-----------------------------+------------+-----------------------------------+");

        for (Country country : countries) {
            System.out.printf("| %-12s | %-39s | %-13s | %-27s | %-10s | %-33s |\n",
                    country.getCountryCode(), country.getName(), country.getContinent(),
                    country.getRegion(), country.getPopulation(), country.getCapital());
        }

        System.out.println("+--------------+-----------------------------------------+---------------+-----------------------------+------------+-----------------------------------+");

        // Check if countries were retrieved
        if (countries.isEmpty()) {
            System.out.println("No countries found.");
        } else {
            System.out.println(countries.size() + " countries found.");
        }
    }


    // Helper function to execute the query and return a list of cities
    private static List<City> getCityList(Connection con, String sql_query) {
        List<City> cities = new ArrayList<>();
        try {
            Statement stmt = con.createStatement();
            ResultSet rset = stmt.executeQuery(sql_query);

            // Populate city list (From Largest to Lowest)
            while (rset.next()) {
                String name = rset.getString("Name");
                String countryName = rset.getString("CountryName"); // Country name from joined country table
                String district = rset.getString("District");
                int population = rset.getInt("Population");

                City city = new City(name, countryName, district, population);
                cities.add(city);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return cities;
    }

    // Function to print cities in a table format
    public static void printCities(List<City> cities, String header) {
        System.out.println("\n\n######## " + header + " ########");
        System.out.println("+------------------------------------+-----------------------------------------+-----------------------------+------------+");
        System.out.printf("| %-34s | %-39s | %-27s | %-10s |\n",
                "City Name", "Country Name", "District", "Population");
        System.out.println("+------------------------------------+-----------------------------------------+-----------------------------+------------+");

        for (City city : cities) {
            System.out.printf("| %-34s | %-39s | %-27s | %-10s |\n",
                    city.getName(), city.getCountryName(), city.getDistrict(), city.getPopulation());
        }

        System.out.println("+------------------------------------+-----------------------------------------+-----------------------------+------------+");

        // Check if countries were retrieved
        if (cities.isEmpty()) {
            System.out.println("No countries found.");
        } else {
            System.out.println(cities.size() + " countries found.");
        }
    }

    public void Table_display(){
        List<Country> countryByWorld = getPopulatedCountries(con, null, null, 0); // Fetch top 10 populated countries
        printCountries(countryByWorld, "---------------------Most populated countries [World]---------------------");
        List<Country> countryByContinent = getPopulatedCountries(con, "Continent", "Europe", 0); // Fetch top 10 populated countries
        printCountries(countryByContinent, "---------------------Most populated countries [Continent] [Europe]---------------------");
        List<Country> countryByRegion = getPopulatedCountries(con, "Region", "Southern and Central Asia", 0); // Fetch top 10 populated countries
        printCountries(countryByRegion, "---------------------World most populated countries [Region] [Southern and Central Asia]---------------------");
        List<Country> Top_10_ByWorld = getPopulatedCountries(con, null, null, 10); // Fetch top 10 populated countries
        printCountries(Top_10_ByWorld, "---------------------Top 10 most populated countries [World]---------------------");
        List<Country> Top_10_ByContinent = getPopulatedCountries(con, "Continent", "North America", 10); // Fetch top 10 populated countries
        printCountries(Top_10_ByContinent, "---------------------Top 10 most populated countries [Continent][North America ]---------------------");
        List<Country> Top_10_ByRegion = getPopulatedCountries(con, "Region", "Caribbean", 10); // Fetch top 10 populated countries
        printCountries(Top_10_ByRegion, "---------------------Top 10 most populated countries [Region][Caribbean]---------------------");

        List<City> cityByWorld = getPopulatedCity(con, null,null,0);
        printCities(cityByWorld, "---------------------Most populated cities [World]---------------------");
        List<City> cityByContinent = getPopulatedCity(con, "Continent","Africa",0);
        printCities(cityByContinent, "---------------------Most populated cities [Continent][Africa]---------------------");
        List<City> cityByRegion = getPopulatedCity(con, "Region","Middle East",0);
        printCities(cityByRegion, "---------------------Most populated cities [Region][Middle East]---------------------");
        List<City> cityByCountry = getPopulatedCity(con, "Name","Russian Federation",0);
        printCities(cityByCountry, "---------------------Most populated cities [Country][Russia]---------------------");
        List<City> cityByDistrict = getPopulatedCity(con, "District","Gelderland",0);
        printCities(cityByDistrict, "---------------------Most populated cities [District][Gelderland]---------------------");

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
    }
//
    public static void main(String[] args) {
        // Create new Application instance
        App app = new App();

        // Connect to database
        app.connect();

        // Set the limit to a positive number to retrieve results
        app.Table_display();

        app.disconnect();
    }
}
