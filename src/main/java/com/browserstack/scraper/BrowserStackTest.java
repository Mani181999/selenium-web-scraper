package com.browserstack.scraper;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import io.github.bonigarcia.wdm.WebDriverManager;

import java.net.URL;

public class BrowserStackTest {
    public static void main(String[] args) {
        // Replace these with your BrowserStack credentials
        String username = "manikumar_9f3c7m";
        String automateKey = "Qour7gS4Ps9sz73qQJe";

        try {
            // Setting up ChromeOptions with BrowserStack capabilities
            ChromeOptions options = new ChromeOptions();
            options.setCapability("bstack:options", new java.util.HashMap<>() {{
                put("os", "Windows");
                put("osVersion", "10");
                put("projectName", "BrowserStack Selenium Test");
                put("buildName", "Build #1");
                put("sessionName", "Google Search Test");
                put("seleniumVersion", "4.12.0"); // Match Selenium version to BrowserStack
            }});
            options.setBrowserVersion("latest");

            // Construct the BrowserStack Hub URL correctly
            String url = "https://" + username + ":" + automateKey + "@hub-cloud.browserstack.com/wd/hub";

            // Initialize WebDriver with BrowserStack Hub URL and options
            //WebDriver driver = new RemoteWebDriver(new URL(url), options); // Use RemoteWebDriver for running tests on a remote server (e.g., BrowserStack)

            // Start the test: Navigate to Google and print the page title
            WebDriverManager.chromedriver().setup();
            WebDriver driver = new ChromeDriver();
            driver.manage().window().maximize();
            driver.get("https://www.google.com");
            System.out.println("Page Title: " + driver.getTitle());

            // End the test: Quit the driver
            driver.quit();
        } catch (Exception e) {
            // Print the stack trace for debugging
            e.printStackTrace();
        }
    }
}
