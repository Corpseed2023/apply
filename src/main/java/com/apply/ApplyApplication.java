package com.apply;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.Duration;
import java.util.List;
import java.util.Set;

@SpringBootApplication
public class ApplyApplication {

	private static final String NAUKRI_URL = "https://www.naukri.com/";
	private static final String EMAIL = "kaushuthakur610@gmail.com";
	private static final String PASSWORD = "kaushu610";

	public static void main(String[] args) {
		SpringApplication.run(ApplyApplication.class, args);
		applyForJobs();
	}

	public static void applyForJobs() {
		System.setProperty("webdriver.chrome.driver", "D:\\chromedriver-win64\\chromedriver.exe");

		ChromeOptions options = new ChromeOptions();
		options.addArguments("--window-size=1920,1080");
		options.addArguments("--disable-blink-features=AutomationControlled"); // Avoid detection
		options.addArguments("--disable-popup-blocking");

		WebDriver driver = new ChromeDriver(options);
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));

		try {
			driver.get(NAUKRI_URL);
			System.out.println("🌐 Opened Naukri.com.");

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
			emailField.sendKeys(EMAIL);
			System.out.println("✅ Entered Email.");

			WebElement passwordField = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@type='password' and contains(@placeholder, 'password')]")));
			passwordField.sendKeys(PASSWORD);
			System.out.println("✅ Entered Password.");

			// 5️⃣ Click Login Button
			WebElement loginSubmit = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'Login')]")));
			loginSubmit.click();
			System.out.println("✅ Login submitted!");

			// 6️⃣ Wait for Login to Complete
			Thread.sleep(5000);
			wait.until(ExpectedConditions.urlContains("naukri.com"));

			if (driver.getCurrentUrl().contains("naukri.com")) {
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
}
