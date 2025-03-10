package com.apply.serviceImpl;

import com.apply.entity.UserCredential;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Set;

@Service
public class NaukriAutomationService {

    public void applyForNaukri(UserCredential userCredential, Set<String> keywords) {
        System.out.println("🔹 Applying on Naukri.com...");
        WebDriver driver = setupWebDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));

        try {
            loginToNaukri(driver, wait, userCredential);
            searchAndApplyForJobs(driver, wait, keywords);
        } finally {
            driver.quit();
            System.out.println("🚪 Closed Naukri browser.");
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

    private void loginToNaukri(WebDriver driver, WebDriverWait wait, UserCredential userCredential) {
        driver.get("https://www.naukri.com/");
        System.out.println("🌐 Opened Naukri.com");

        try {
            WebElement emailField = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@type='text' and contains(@placeholder, 'Email')]")));
            emailField.sendKeys(userCredential.getUsername());

            WebElement passwordField = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@type='password']")));
            passwordField.sendKeys(userCredential.getPassword());

            WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'Login')]")));
            loginButton.click();
            System.out.println("✅ Logged in!");

            Thread.sleep(5000);
            wait.until(ExpectedConditions.urlContains("home"));

        } catch (Exception e) {
            System.err.println("❌ Login Failed: " + e.getMessage());
            driver.quit();
        }
    }

    private void searchAndApplyForJobs(WebDriver driver, WebDriverWait wait, Set<String> keywords) {
        try {
            for (String keyword : keywords) {
                System.out.println("🔍 Searching for: " + keyword);

                WebElement searchBox = wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//input[contains(@class, 'suggestor-input') and contains(@placeholder, 'Enter keyword')]")
                ));
                searchBox.clear();
                searchBox.sendKeys(keyword);

                WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[contains(@class, 'nI-gNb-sb__icon-wrapper')]")
                ));
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", searchButton);
                System.out.println("🔍 Clicked Search button for keyword: " + keyword);

                Thread.sleep(5000);
                List<WebElement> jobCards = driver.findElements(By.xpath("//a[contains(@class, 'title')]"));

                int applyLimit = 5;
                int appliedCount = 0;
                for (WebElement jobCard : jobCards) {
                    if (appliedCount >= applyLimit) break;
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", jobCard);
                    System.out.println("✅ Clicked on job");

                    Thread.sleep(5000);
                    WebElement applyButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'Apply')]")));
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", applyButton);
                    System.out.println("✅ Applied to job");
                    appliedCount++;
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Error while searching/applying for jobs: " + e.getMessage());
        }
    }
}
