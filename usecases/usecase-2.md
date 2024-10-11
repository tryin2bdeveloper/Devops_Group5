### USE CASE: 2 Generate Continent-Specific Population Report from Largest to Smallest Distribution

## CHARACTERISTIC INFORMATION
## Goal in Context
As an analyst, I want to generate a report listing all countries in a continent, sorted by population size from largest to smallest. 
So, I can support reporting for population data within the organization.

## Scope
Small organizations.

## Level
Primary task.

## Preconditions
The organization must have access to the population data for all countries.

## Success End Condition
A report is available showing the population for each country.
So, it is including whether people live in cities or rural areas.

## Failed End Condition
The report is not produced

## Primary Actor
Analysis team.

## Trigger
A request for population data analysis is submitted.

## MAIN SUCCESS SCENARIO
The analyst requests the population data.  
The system to a list of populations is fetched for all countries in the specified continent.  
The system is sorted by population size from largest to smallest for data.  
The report is successfully displayed for the analyst, differentiating between city and rural populations where applicable.

## EXTENSIONS
Data requested by the analyst does not exist.

## SUB-VARIATIONS
None.

## SCHEDULE
DUE DATE: Release 1.0