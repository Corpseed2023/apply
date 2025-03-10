package com.apply.serviceImpl;

import com.apply.entity.UserCredential;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;

@Service
public class LinkedInAutomationService {

    public void applyForLinkedIn(UserCredential userCredential, Set<String> keywords) {
        System.out.println("🔹 Applying on LinkedIn...");
        WebDriver driver = setupWebDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));

        try {
            loginToLinkedIn(driver, wait, userCredential);
            searchAndApplyForJobs(driver, wait, keywords);
        } finally {
            driver.quit();
            System.out.println("🚪 Closed LinkedIn browser.");
        }
    }

    private WebDriver setupWebDriver() {
        System.setProperty("webdriver.chrome.driver", "D:\\chromedriver-win64\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--disable-popup-blocking");

        return new ChromeDriver(options);
    }

    private void loginToLinkedIn(WebDriver driver, WebDriverWait wait, UserCredential userCredential) {
        driver.get("https://www.linkedin.com/login");
        System.out.println("🌐 Opened LinkedIn");

        try {
            WebElement emailField = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("username")));
            emailField.sendKeys(userCredential.getUsername());

            WebElement passwordField = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("password")));
            passwordField.sendKeys(userCredential.getPassword());

            WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'Sign in')]")));
            loginButton.click();
            System.out.println("✅ Logged in!");

            Thread.sleep(5000);
            wait.until(ExpectedConditions.urlContains("feed"));

        } catch (Exception e) {
            System.err.println("❌ Login Failed: " + e.getMessage());
            driver.quit();
        }
    }

    private void searchAndApplyForJobs(WebDriver driver, WebDriverWait wait, Set<String> keywords) {
        for (String keyword : keywords) {
            System.out.println("🔍 Searching for: " + keyword);
            // Implement search and apply logic for LinkedIn
        }
    }
}
