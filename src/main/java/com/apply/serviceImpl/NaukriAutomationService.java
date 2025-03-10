package com.apply.serviceImpl;

import com.apply.entity.Question;
import com.apply.entity.UserCredential;
import com.apply.repository.QuestionRepository;
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
public class NaukriAutomationService {

    @Autowired
    private QuestionRepository questionRepository;

    public void applyForNaukri(UserCredential userCredential, Set<String> keywords) {
        System.out.println("🔹 Applying on Naukri.com...");
        WebDriver driver = setupWebDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));

        try {
            loginToNaukri(driver, wait, userCredential);
            searchAndApplyForJobs(driver, wait, userCredential, keywords);
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
            closePopupIfExists(driver, wait);

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

    private void searchAndApplyForJobs(WebDriver driver, WebDriverWait wait, UserCredential userCredential, Set<String> keywords) {
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
        } catch (Exception e) {
            System.err.println("❌ Error while searching/applying for jobs: " + e.getMessage());
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

    private void closePopupIfExists(WebDriver driver, WebDriverWait wait) {
        try {
            WebElement closePopup = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'Close')]")));
            closePopup.click();
            System.out.println("✅ Closed a popup.");
        } catch (Exception ignored) {
            System.out.println("⚠ No popup found.");
        }
    }
}
