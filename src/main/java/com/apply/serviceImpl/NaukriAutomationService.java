package com.apply.serviceImpl;

import com.apply.entity.Platform;
import com.apply.entity.Question;
import com.apply.entity.User;
import com.apply.entity.UserCredential;
import com.apply.repository.PlatformRepository;
import com.apply.repository.QuestionRepository;
import com.apply.repository.UserRepository;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;

@Service
public class NaukriAutomationService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlatformRepository platformRepository;

    public void applyForNaukri(UserCredential userCredential, Set<String> keywords) {
        System.out.println("🧙 [Naukri] Starting automation for: " + userCredential.getUsername());

        WebDriver driver = setupWebDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));

        try {
            loginToNaukri(driver, wait, userCredential);

            Platform platform = platformRepository.findByName("Naukri")
                    .orElseGet(() -> platformRepository.save(new Platform("Naukri")));

            User dbUser = userCredential.getUser();
            if (dbUser.getId() == null) {
                Optional<User> existingUser = userRepository.findByEmail(dbUser.getEmail());
                if (existingUser.isPresent()) {
                    dbUser = existingUser.get();
                } else {
                    dbUser = userRepository.save(dbUser);
                }
            }


            for (String keyword : keywords) {
                System.out.println("🔍 Searching jobs for keyword: " + keyword);
                searchJobs(driver, wait, keyword);
                applyToJobs(driver, wait, dbUser, platform);
            }

        } catch (Exception e) {
            System.err.println("🚨 Error during automation: " + e.getMessage());
            e.printStackTrace();
        } finally {
            driver.quit();
            System.out.println("🚪 Chrome closed.");
        }
    }

    private WebDriver setupWebDriver() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--window-size=1920,1080", "--disable-popup-blocking");
        return new ChromeDriver(options);
    }

    private void loginToNaukri(WebDriver driver, WebDriverWait wait, UserCredential credential) {
        driver.get("https://www.naukri.com/");
        System.out.println("🌐 Opening Naukri...");

        try {
            WebElement loginAnchor = wait.until(ExpectedConditions.elementToBeClickable(By.id("login_Layer")));
            loginAnchor.click();
            System.out.println("➡ Clicked login");

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("login-layer")));
            WebElement email = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//input[@type='text' and @placeholder='Enter your active Email ID / Username']")));
            WebElement password = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//input[@type='password' and @placeholder='Enter your password']")));
            WebElement loginBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[@type='submit' and contains(@class,'loginButton')]")));

            email.clear(); email.sendKeys(credential.getUsername());
            password.clear(); password.sendKeys(credential.getPassword());
            loginBtn.click();

            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("login-layer")));
            System.out.println("🎉 Login successful");

            closeChatPopupIfVisible(driver);
        } catch (Exception e) {
            throw new RuntimeException("❌ Login failed: " + e.getMessage());
        }
    }

    private void closeChatPopupIfVisible(WebDriver driver) {
        try {
            List<WebElement> closeBtns = driver.findElements(By.cssSelector(".chatBot-ic-cross"));
            if (!closeBtns.isEmpty() && closeBtns.get(0).isDisplayed()) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", closeBtns.get(0));
                System.out.println("🚫 Chat popup closed.");
            }
        } catch (Exception ignored) {
            System.out.println("ℹ️ No chatbot popup found.");
        }
    }

    private void searchJobs(WebDriver driver, WebDriverWait wait, String keyword) throws InterruptedException {
        WebElement searchBox = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//input[contains(@placeholder, 'Enter keyword')]")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].removeAttribute('tabindex')", searchBox);
        ((JavascriptExecutor) driver).executeScript("arguments[0].focus();", searchBox);
        searchBox.sendKeys(Keys.CONTROL + "a", Keys.DELETE, keyword);

        WebElement searchBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button.nI-gNb-sb__icon-wrapper")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", searchBtn);

        Thread.sleep(5000);
    }

    private void applyToJobs(WebDriver driver, WebDriverWait wait, User user, Platform platform) throws InterruptedException {
        List<WebElement> jobs = driver.findElements(By.xpath("//a[contains(@class, 'title')]"));
        System.out.println("📄 Jobs found: " + jobs.size());

        int count = 0;
        for (WebElement job : jobs) {
            if (count++ >= 3) break;

            String title = job.getText().trim();
            System.out.println("➡ Applying to job: " + title);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", job);
            Thread.sleep(5000);

            switchToNewTab(driver);

            try {
                WebElement applyBtn = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[@id='apply-button' or contains(@class,'apply-button')]")));
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", applyBtn);
                System.out.println("✅ Applied to: " + title);

                Thread.sleep(2000);
                boolean anyQuestionHandled = handleRecruiterQuestions(driver, user, platform, title);
                if (anyQuestionHandled) {
                    System.out.println("✅ All recruiter questions answered for: " + title);
                } else {
                    System.out.println("🛑 No recruiter questions found. Please consider adding default questions for: " + title);
                }
            } catch (Exception e) {
                System.out.println("⚠ Could not apply: " + title);
            }

            driver.close();
            switchToMainTab(driver);
        }
    }



    private void switchToNewTab(WebDriver driver) {
        String main = driver.getWindowHandle();
        for (String handle : driver.getWindowHandles()) {
            if (!handle.equals(main)) {
                driver.switchTo().window(handle);
                break;
            }
        }
    }

    private void switchToMainTab(WebDriver driver) {
        driver.switchTo().window(driver.getWindowHandles().iterator().next());
    }

    private boolean handleRecruiterQuestions(WebDriver driver, User user, Platform platform, String jobTitle) {
        List<WebElement> questions = driver.findElements(
                By.xpath("//div[contains(@class, 'chatbot_MessageContainer')]//li[contains(@class, 'botItem')]//span")
        );

        Set<String> seen = new HashSet<>();
        boolean anyQuestionFound = false;

        for (WebElement q : questions) {
            String text = q.getText().trim();
            if (text.isEmpty() || text.toLowerCase().startsWith("hi") || !seen.add(text)) continue;

            anyQuestionFound = true;
            System.out.println("❓ Question: " + text);

            Optional<Question> existing = questionRepository.findByPlatformAndQuestion(platform, text);
            if (existing.isEmpty()) {
                Question newQ = new Question();
                newQ.setPlatform(platform);
                newQ.setUser(user);
                newQ.setQuestion(text);
                questionRepository.save(newQ);
            } else {
                String answer = existing.get().getAnswer();
                if (answer != null && !answer.isBlank()) {
                    try {
                        WebElement inputBox = driver.findElement(By.cssSelector("div.textArea[contenteditable='true']"));
                        inputBox.click();
                        inputBox.sendKeys(answer);
                        System.out.println("✍️ Answered: " + answer);
                    } catch (Exception ignored) {
                        System.out.println("⚠ Couldn't fill answer.");
                    }
                }
            }
        }

        return anyQuestionFound;
    }
}
