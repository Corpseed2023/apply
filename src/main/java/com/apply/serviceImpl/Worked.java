package com.apply.serviceImpl;

import com.apply.entity.Question;
import com.apply.entity.UserCredential;
import com.apply.entity.Platform;
import com.apply.repository.QuestionRepository;
import com.apply.repository.UserCredentialRepository;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class Worked {

    @Autowired
    private UserCredentialRepository userCredentialRepository;

    @Autowired
    private QuestionRepository questionRepository;

    public void applyForAllUsers() {
        List<UserCredential> users = userCredentialRepository.findAll();
        for (UserCredential user : users) {
            System.out.println("🚀 Applying for user: " + user.getUsername() + " on platform: " + user.getPlatform().getName());
            applyForJobs(user);
        }
    }

    public void applyForJobs(UserCredential userCredential) {
        System.setProperty("webdriver.chrome.driver", "D:\\chromedriver-win64\\chromedriver.exe");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--disable-popup-blocking");

        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));

        try {
            Platform platform = userCredential.getPlatform();
            String username = userCredential.getUsername();
            String password = userCredential.getPassword();

            driver.get(getPlatformUrl(platform.getName()));
            System.out.println("🌐 Opened " + platform.getName());

            // Handle popups
            closePopupIfExists(driver, wait);

            // Login Process
            loginToPlatform(driver, wait, username, password);

            // Search for Jobs
            searchForJobs(driver, wait);

            // Apply to Jobs
            applyToJobs(driver, wait, userCredential);

        } catch (Exception e) {
            System.err.println("🚨 Error during job application: " + e.getMessage());
        } finally {
            driver.quit();
            System.out.println("🚪 Browser closed.");
        }
    }

    private void closePopupIfExists(WebDriver driver, WebDriverWait wait) {
        try {
            WebElement closePopup = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'Close')]")));
            closePopup.click();
            System.out.println("✅ Closed a popup.");
        } catch (Exception ignored) {
            System.out.println("⚠ No popup found.");
        }
    }

    private void loginToPlatform(WebDriver driver, WebDriverWait wait, String username, String password) throws InterruptedException {
        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(@href, 'login') or contains(text(),'Login')]")));
        loginButton.click();
        System.out.println("✅ Clicked on Login button.");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class, 'login-layer')]")));

        WebElement emailField = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@type='text' and contains(@placeholder, 'Email')]")));
        emailField.sendKeys(username);
        System.out.println("✅ Entered Email.");

        WebElement passwordField = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@type='password' and contains(@placeholder, 'password')]")));
        passwordField.sendKeys(password);
        System.out.println("✅ Entered Password.");

        WebElement loginSubmit = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'Login')]")));
        loginSubmit.click();
        System.out.println("✅ Login submitted!");

        Thread.sleep(5000);
        wait.until(ExpectedConditions.urlContains("home"));

        if (driver.getCurrentUrl().contains("home")) {
            System.out.println("🎉 Successfully Logged In!");
        } else {
            throw new RuntimeException("❌ Login Failed! Check CAPTCHA or credentials.");
        }
    }

    private void searchForJobs(WebDriver driver, WebDriverWait wait) {
        WebElement searchBox = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//input[contains(@class, 'suggestor-input') and contains(@placeholder, 'Enter keyword')]")
        ));
        searchBox.sendKeys("Java Developer");
        System.out.println("✅ Entered search term: Java Developer");

        WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(@class, 'nI-gNb-sb__icon-wrapper')]")
        ));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", searchButton);
        System.out.println("🔍 Clicked Search button.");

        try {
            Thread.sleep(5000);
            System.out.println("📜 Job listings loaded.");
        } catch (InterruptedException ignored) {}
    }

    private void applyToJobs(WebDriver driver, WebDriverWait wait, UserCredential userCredential) throws InterruptedException {
        List<WebElement> jobCards = driver.findElements(By.xpath("//a[contains(@class, 'title')]"));

        int applyLimit = 5;
        int appliedCount = 0;
        String originalWindow = driver.getWindowHandle();
        Set<String> oldWindows = driver.getWindowHandles();

        for (WebElement jobCard : jobCards) {
            if (appliedCount >= applyLimit) break;

            String jobTitle = jobCard.getText().trim();
            System.out.println("🔍 Found Job: " + jobTitle);

            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", jobCard);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", jobCard);

            Thread.sleep(10000);

            Set<String> newWindows = driver.getWindowHandles();
            newWindows.removeAll(oldWindows);
            if (!newWindows.isEmpty()) {
                driver.switchTo().window(newWindows.iterator().next());
                System.out.println("🔄 Switched to job details page: " + jobTitle);
            } else {
                System.err.println("❌ New tab did not open for job: " + jobTitle);
                continue;
            }

            wait.until(ExpectedConditions.jsReturnsValue("return document.readyState === 'complete';"));

            try {
                WebElement applyButton = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[contains(text(),'Apply')]")
                ));
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", applyButton);
                System.out.println("✅ Applied to: " + jobTitle);
                appliedCount++;
            } catch (Exception e) {
                System.err.println("❌ Apply button not found for: " + jobTitle);
            }

            handleChatbotQuestions(driver, wait, userCredential);

            driver.close();
            driver.switchTo().window(originalWindow);
            System.out.println("🔄 Switched back to job listings page.");
            Thread.sleep(2000);
        }
    }

    private void handleChatbotQuestions(WebDriver driver, WebDriverWait wait, UserCredential userCredential) {
        try {
            List<WebElement> chatbotQuestions = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                    By.xpath("//ul/li//span")
            ));

            for (WebElement questionElement : chatbotQuestions) {
                String questionText = questionElement.getText().trim();
                Optional<Question> existingQuestion = questionRepository.findByPlatformAndQuestion(userCredential.getPlatform(), questionText);

                if (existingQuestion.isPresent()) {
                    Question question = existingQuestion.get();
                    if (!question.getAnswer().isEmpty()) {
                        WebElement answerInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@contenteditable='true']")));
                        answerInput.sendKeys(question.getAnswer());
                        System.out.println("🤖 Answered: " + questionText);
                    }
                } else {
                    questionRepository.save(new Question(null, userCredential.getUser(), userCredential.getPlatform(), questionText, ""));
                    System.out.println("🤖 Saved new question: " + questionText);
                }
            }
        } catch (Exception ignored) {}
    }

    private String getPlatformUrl(String platform) {
        return switch (platform.toLowerCase()) {
            case "naukri" -> "https://www.naukri.com/";
            case "linkedin" -> "https://www.linkedin.com/";
            default -> throw new IllegalArgumentException("Unsupported platform: " + platform);
        };
    }
}
