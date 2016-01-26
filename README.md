## Site Scraper

This is a console application that scrapes the Sainsbury’s grocery site - Ripe Fruits page and returns a JSON array of all the products on the page.

## How to run the app

Go to the project folder and execute the following command:
```
mvn clean package -DskipTests=true && java -jar target/site-scraper-0.0.1-SNAPSHOT.jar
```
## How to run tests
```
mvn clean test
```
## About my implementation 

- A log file called SiteScraperApp.log is created in target folder 

- I used Spring Boot and create the initial project at **http://start.spring.io/**

- Implemented CommandLineRunner interface to create a console application with Spring Boot

- Used profiles, so testing code uses the test profile (application-test.properties)

- Used Mockito framework to mock HtmlPage, so tests load pages from local resource files instead of accessing Internet.

- Used HtmlUnit framework to browse the page, which has fairly good JavaScript support. Also, used XPath query to find the html elements we need.

- Used the BDD style test to cover the whole use case and used unit tests to test particular methods. 
- 

## Feedback and comments

Welcome to any feedback and comments. Please email **Patrick Zhang** **pzcareer@gmail.com**.

