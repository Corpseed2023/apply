package com.apply.serviceImpl;

import com.apply.entity.Question;
import com.apply.entity.UserCredential;
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
public class AutomationService {

    @Autowired
    private UserCredentialRepository userCredentialRepository;

    @Autowired
    private QuestionRepository questionRepository;

    public void applyForAllUsers() {
        // Fetch all users from the database
        List<UserCredential> users = userCredentialRepository.findAll();

        // Iterate over each user and apply for jobs
        for (UserCredential user : users) {
            System.out.println("🚀 Applying jobs for user: " + user.getUsername() + " on platform: " + user.getPlatform());
            applyForJobs(user);
        }
    }

    public void applyForJobs(UserCredential userCredential) {
        // Set the path to the ChromeDriver executable
        System.setProperty("webdriver.chrome.driver", "D:\\chromedriver-win64\\chromedriver.exe");

        // Configure Chrome options
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-blink-features=AutomationControlled"); // Avoid detection
        options.addArguments("--disable-popup-blocking");

        // Initialize WebDriver and WebDriverWait
        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));

        try {
            String platform = userCredential.getPlatform();
            String username = userCredential.getUsername();
            String password = userCredential.getPassword();

            // Open the platform URL
            String platformUrl = getPlatformUrl(platform);
            driver.get(platformUrl);
            System.out.println("🌐 Opened " + platform + ".");

            // 1️⃣ Close Popups if Present
            try {
                WebElement closePopup = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'Close')]")));
                closePopup.click();
                System.out.println("✅ Closed a popup.");
            } catch (Exception ignored) {
                System.out.println("⚠ No popup found.");
            }

            // 2️⃣ Locate & Click Login Button
            WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(@href, 'login') or contains(text(),'Login')]")));
            loginButton.click();
            System.out.println("✅ Clicked on Login button.");

            // 3️⃣ Wait for Login Modal
            WebElement loginModal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class, 'login-layer')]")));
            System.out.println("✅ Login modal is now visible.");

            // 4️⃣ Enter Email & Password
            WebElement emailField = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@type='text' and contains(@placeholder, 'Email')]")));
            emailField.sendKeys(username); // Use user's username
            System.out.println("✅ Entered Email.");

            WebElement passwordField = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@type='password' and contains(@placeholder, 'password')]")));
            passwordField.sendKeys(password); // Use user's password
            System.out.println("✅ Entered Password.");

            // 5️⃣ Click Login Button
            WebElement loginSubmit = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'Login')]")));
            loginSubmit.click();
            System.out.println("✅ Login submitted!");

            // 6️⃣ Wait for Login to Complete
            Thread.sleep(5000);
            wait.until(ExpectedConditions.urlContains("home"));

            if (driver.getCurrentUrl().contains("home")) {
                System.out.println("🎉 Successfully Logged In!");
            } else {
                System.err.println("❌ Login Failed! Check if CAPTCHA is blocking it.");
                driver.quit();
                return;
            }

            // 7️⃣ Ensure Full Page Load Before Searching
            wait.until(ExpectedConditions.jsReturnsValue("return document.readyState === 'complete';"));
            System.out.println("✅ Page fully loaded after login.");

            // 8️⃣ **Find Search Box & Enter Search Term**
            WebElement searchBox = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//input[contains(@class, 'suggestor-input') and contains(@placeholder, 'Enter keyword')]")
            ));
            searchBox.sendKeys("Java Developer");
            System.out.println("✅ Entered search term: Java Developer");

            // 🔟 **Click the "Search" Button Using JavaScript**
            WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(@class, 'nI-gNb-sb__icon-wrapper')]")
            ));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", searchButton);
            System.out.println("🔍 Clicked Search button.");

            // 11️⃣ **Ensure Job Listings Load**
            Thread.sleep(5000);  // Allow jobs to load
            System.out.println("📜 Job listings loaded.");

            // 12️⃣ **Click on Job Cards and Apply**
            List<WebElement> jobCards = driver.findElements(By.xpath("//a[contains(@class, 'title')]"));

            int applyLimit = 5;
            int appliedCount = 0;
            String originalWindow = driver.getWindowHandle();
            Set<String> oldWindows = driver.getWindowHandles();

            for (WebElement jobCard : jobCards) {
                if (appliedCount >= applyLimit) break;

                // Get job name
                String jobTitle = jobCard.getText().trim();
                System.out.println("🔍 Found Job: " + jobTitle);

                // Click the job card
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", jobCard);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", jobCard);
                System.out.println("✅ Clicked on job: " + jobTitle);

                // Wait for 6 seconds before applying
                Thread.sleep(10000);
                System.out.println("⏳ Waiting 6 seconds before applying to: " + jobTitle);

                // Wait for new tab and switch to it
                Set<String> newWindows = driver.getWindowHandles();
                newWindows.removeAll(oldWindows);
                if (!newWindows.isEmpty()) {
                    String newTab = newWindows.iterator().next();
                    driver.switchTo().window(newTab);
                    System.out.println("🔄 Switched to job details page: " + jobTitle);
                } else {
                    System.err.println("❌ New tab did not open for job: " + jobTitle);
                    continue;
                }

                // Wait for Job Details Page to Load
                wait.until(ExpectedConditions.jsReturnsValue("return document.readyState === 'complete';"));
                System.out.println("✅ Job details page loaded: " + jobTitle);

                // Click the Apply button
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

                // Handle Chatbot if it appears
                try {
                    // Wait for the chatbot container to appear
                    WebElement chatbotContainer = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@id, 'Messages')]")));
                    if (chatbotContainer.isDisplayed()) {
                        System.out.println("🤖 Chatbot detected.");

                        // Wait for the chatbot questions to load
                        List<WebElement> chatbotQuestions = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                                By.xpath("/html/body/div[2]/div/div[1]/div[2]/ul/li//span")
                        ));

                        // Iterate through each question
                        for (WebElement questionElement : chatbotQuestions) {
                            String questionText = questionElement.getText().trim();
                            System.out.println("🤖 Chatbot question: " + questionText);

                            // Check if the question exists in the database
                            Optional<Question> existingQuestion = questionRepository.findByPlatformAndQuestion(platform, questionText);

                            if (existingQuestion.isPresent()) {
                                // If the question exists and has an answer, answer it
                                Question question = existingQuestion.get();
                                if (question.getAnswer() != null && !question.getAnswer().isEmpty()) {
                                    WebElement answerInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@id, 'InputBox') and @contenteditable='true']")));
                                    answerInput.sendKeys(question.getAnswer());
                                    System.out.println("🤖 Answered the chatbot question: " + questionText);

                                    // Submit the answer
                                    WebElement sendButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[contains(@id, 'sendMsg')]//div[@class='sendMsg']")));
                                    sendButton.click();
                                    System.out.println("🤖 Submitted the answer.");
                                } else {
                                    System.err.println("⚠ Question exists in the database but has no answer: " + questionText);
                                }
                            } else {
                                // If the question does not exist, save it to the database
                                Question newQuestion = new Question();
                                newQuestion.setPlatform(platform);
                                newQuestion.setQuestion(questionText);
                                newQuestion.setAnswer(""); // Save with an empty answer
                                questionRepository.save(newQuestion);
                                System.out.println("🤖 Saved new question to the database: " + questionText);
                            }
                        }
                    }
                } catch (Exception e) {
                    System.err.println("⚠ No chatbot detected or error handling chatbot: " + e.getMessage());
                }

                // Close the job details tab & switch back
                driver.close();
                driver.switchTo().window(originalWindow);
                System.out.println("🔄 Switched back to job listings page.");

                // Small delay before next job
                Thread.sleep(2000);
            }

            System.out.println("🎯 Successfully applied for " + appliedCount + " jobs.");

        } catch (Exception e) {
            System.err.println("🚨 Error during job application: " + e.getMessage());
        } finally {
            driver.quit();
            System.out.println("🚪 Closed browser.");
        }
    }

    private String getPlatformUrl(String platform) {
        switch (platform.toLowerCase()) {
            case "naukri":
                return "https://www.naukri.com/";
            case "linkedin":
                return "https://www.linkedin.com/";
            // Add more platforms as needed
            default:
                throw new IllegalArgumentException("Unsupported platform: " + platform);
        }
    }
}