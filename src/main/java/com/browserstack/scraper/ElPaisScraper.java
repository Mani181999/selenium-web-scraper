package com.browserstack.scraper;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class ElPaisScraper {

    public static void main(String[] args) {
        // Set up WebDriver using WebDriverManager
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS); // Implicit wait

        try {
            // Navigate to El País website
            driver.get("https://elpais.com/");
            System.out.println("Page Title: " + driver.getTitle());

            // Handle potential cookie pop-ups or overlays
            dismissOverlays(driver);

            // Navigate to the "Opinión" section
            openOpinionSection(driver);

            // Extract articles
            List<Map<String, String>> articles = scrapeArticles(driver);

            // Display articles
            for (Map<String, String> article : articles) {
                System.out.println("Title (Spanish): " + article.get("title"));
                System.out.println("Content: " + article.get("content"));
                System.out.println("Image URL: " + article.get("imageUrl"));
                System.out.println("-----");
            }

            // Translate titles
            List<String> translatedTitles = translateTitles(articles);

            // Analyze translated headers
            Map<String, Integer> wordCounts = analyzeTranslatedHeaders(translatedTitles);

            // Print repeated words
            System.out.println("\nRepeated Words in Translated Titles:");
            for (Map.Entry<String, Integer> entry : wordCounts.entrySet()) {
                if (entry.getValue() > 2) {
                    System.out.println(entry.getKey() + ": " + entry.getValue());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close the browser
            driver.quit();
        }
    }

    // Dismiss overlays or cookie pop-ups
    public static void dismissOverlays(WebDriver driver) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            // Locate and dismiss the cookie pop-up (adjust CSS selector if needed)
            WebElement cookieButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button[aria-label='Aceptar']"))); 
            cookieButton.click();
            System.out.println("Cookie pop-up dismissed.");
        } catch (TimeoutException | NoSuchElementException e) {
            System.out.println("No cookie pop-up found.");
        } catch (Exception e) {
            System.out.println("Error dismissing overlays: " + e.getMessage());
        }
    }

    // Navigate to the "Opinión" section
    public static void openOpinionSection(WebDriver driver) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement opinionSection = wait.until(
                    ExpectedConditions.elementToBeClickable(By.cssSelector("a[href*='/opinion/']"))); 
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", opinionSection);
            System.out.println("Navigated to 'Opinión' section.");
        } catch (Exception e) {
            System.out.println("Failed to navigate to 'Opinión' section: " + e.getMessage());
        }
    }

    // Scrape articles
    public static List<Map<String, String>> scrapeArticles(WebDriver driver) {
        List<Map<String, String>> articles = new ArrayList<>();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            List<WebElement> articleElements = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("article")));

            for (int i = 0; i < Math.min(articleElements.size(), 5); i++) {
                WebElement article = articleElements.get(i);
                Map<String, String> articleData = new HashMap<>();

                // Extract title
                try {
                    String title = article.findElement(By.cssSelector("h2")).getText();
                    articleData.put("title", title);
                } catch (NoSuchElementException e) {
                    articleData.put("title", "Title not available");
                }

                // Extract content (attempt with alternative selectors)
                try {
                    WebElement contentElement = article.findElement(By.cssSelector(".article-content")); 
                    articleData.put("content", contentElement.getText());
                } catch (NoSuchElementException e) {
                    try {
                        WebElement contentElement = article.findElement(By.xpath(".//div[@class='articulo_cuerpo']")); // Example alternative XPath
                        articleData.put("content", contentElement.getText());
                    } catch (NoSuchElementException ex) {
                        articleData.put("content", "Content not available");
                    }
                }

                // Extract image URL
                try {
                    WebElement imgElement = article.findElement(By.tagName("img"));
                    String imageUrl = imgElement.getAttribute("src");
                    articleData.put("imageUrl", imageUrl);
                    downloadImage(imageUrl, "image" + i + ".jpg");
                } catch (NoSuchElementException e) {
                    articleData.put("imageUrl", "Image not available");
                }

                articles.add(articleData);
            }
        } catch (Exception e) {
            System.out.println("Error scraping articles: " + e.getMessage());
        }

        return articles;
    }

    // Download images
    public static void downloadImage(String imageUrl, String destinationFile) {
        try {
            URL url = new URL(imageUrl);
            InputStream in = url.openStream();
            Files.copy(in, Paths.get(destinationFile), StandardCopyOption.REPLACE_EXISTING);
            in.close();
        } catch (IOException e) {
            System.out.println("Failed to download image: " + e.getMessage());
        }
    }

    // Translate titles using Google Translate API
    public static List<String> translateTitles(List<Map<String, String>> articles) throws IOException {
        List<String> translatedTitles = new ArrayList<>();
        for (Map<String, String> article : articles) {
            String title = article.get("title");
            String translatedTitle = GoogleTranslate.translateText(title, "es", "en"); 
            translatedTitles.add(translatedTitle);
        }
        return translatedTitles;
    }

    // Analyze translated headers
    public static Map<String, Integer> analyzeTranslatedHeaders(List<String> translatedTitles) {
        Map<String, Integer> wordCounts = new HashMap<>();
        for (String title : translatedTitles) {
            String[] words = title.split("\\s+"); 
            for (String word : words) {
                wordCounts.put(word, wordCounts.getOrDefault(word, 0) + 1);
            }
        } 
        return wordCounts;
    }
}