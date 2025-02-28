package com.apply.serviceImpl;

import com.apply.entity.ApplicationHistory;
import com.apply.entity.Question;
import com.apply.entity.UserCredential;
import com.apply.repository.ApplicationHistoryRepository;
import com.apply.repository.QuestionRepository;
import com.apply.repository.UserCredentialRepository;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AutomationService {

    @Autowired
    private  UserCredentialRepository userCredentialRepository;

    @Autowired
    private  ApplicationHistoryRepository applicationHistoryRepository;

    @Autowired
    private  QuestionRepository questionRepository;

    public String applyFor(String platform, String jobTitle) {
        Optional<UserCredential> credentialsOpt = userCredentialRepository.findByPlatform(platform);
        if (credentialsOpt.isEmpty()) {
            return "No credentials found for platform: " + platform;
        }

        UserCredential credentials = credentialsOpt.get();

        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            driver.manage().window().maximize();
            driver.get(getPlatformURL(platform));

            loginToPlatform(driver, wait, platform, credentials);
            applyToJobs(driver, wait, platform, jobTitle);

        } catch (Exception e) {
            e.printStackTrace();
            return "Error during job application: " + e.getMessage();
        } finally {
            driver.quit();
        }

        return "Job applications completed on " + platform;
    }

    private void loginToPlatform(WebDriver driver, WebDriverWait wait, String platform, UserCredential credentials) {
        if (platform.equalsIgnoreCase("LinkedIn")) {
            driver.findElement(By.id("session_key")).sendKeys(credentials.getUsername());
            driver.findElement(By.id("session_password")).sendKeys(credentials.getPassword());
            driver.findElement(By.xpath("//button[@type='submit']")).click();
        } else if (platform.equalsIgnoreCase("Naukri")) {
            driver.findElement(By.id("usernameField")).sendKeys(credentials.getUsername());
            driver.findElement(By.id("passwordField")).sendKeys(credentials.getPassword());
            driver.findElement(By.xpath("//button[contains(text(),'Login')]")).click();
        }

        wait.until(ExpectedConditions.urlContains("home"));
    }

    private void applyToJobs(WebDriver driver, WebDriverWait wait, String platform, String jobTitle) {
        if (platform.equalsIgnoreCase("LinkedIn")) {
            driver.get("https://www.linkedin.com/jobs/search/");
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@placeholder='Search jobs']")))
                    .sendKeys(jobTitle + Keys.ENTER);

            List<WebElement> jobListings = driver.findElements(By.xpath("//div[contains(@class,'job-card-container')]"));
            for (WebElement job : jobListings) {
                try {
                    job.click();
                    wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'Easy Apply')]"))).click();

                    // Handle application questions
                    boolean stopped = checkForQuestions(driver, platform);
                    if (stopped) return;

                    uploadResume(driver, wait);
                    storeApplicationHistory(platform, jobTitle, "LinkedIn Company");
                } catch (Exception ignored) {}
            }

        }
    }

    private boolean checkForQuestions(WebDriver driver, String platform) {
        List<WebElement> questionElements = driver.findElements(By.className("job-question"));
        for (WebElement questionElement : questionElements) {
            String questionText = questionElement.getText();
            Optional<Question> existingQuestion = questionRepository.findByPlatformAndQuestion(platform, questionText);

            if (existingQuestion.isEmpty()) {
                Question newQuestion = new Question();
                newQuestion.setPlatform(platform);
                newQuestion.setQuestion(questionText);
                newQuestion.setAnswer("Please review and answer manually.");
                questionRepository.save(newQuestion);
                driver.quit();
                return true;
            }
        }
        return false;
    }

    private void uploadResume(WebDriver driver, WebDriverWait wait) {
        File resumeFile = new File("D:/resumes/my_resume.pdf");
        WebElement uploadButton = driver.findElement(By.xpath("//input[@type='file']"));
        uploadButton.sendKeys(resumeFile.getAbsolutePath());
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'Submit')]"))).click();
    }

    private void storeApplicationHistory(String platform, String jobTitle, String company) {
        ApplicationHistory applicationHistory = new ApplicationHistory();
        applicationHistory.setPlatform(platform);
        applicationHistory.setJobTitle(jobTitle);
        applicationHistory.setCompany(company);
        applicationHistory.setAppliedDate(new Date());
        applicationHistoryRepository.save(applicationHistory);
    }

    private String getPlatformURL(String platform) {
        return switch (platform.toLowerCase()) {
            case "linkedin" -> "https://www.linkedin.com/login";
            case "naukri" -> "https://www.naukri.com/";
            default -> throw new RuntimeException("Unsupported platform");
        };
    }
}
