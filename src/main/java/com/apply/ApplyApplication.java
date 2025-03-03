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
			try {
				WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(@href, 'login') or contains(text(),'Login')]")));
				loginButton.click();
				System.out.println("✅ Clicked on Login button.");
			} catch (Exception e) {
				System.err.println("❌ Login button not found! Retrying with alternative method...");
				WebElement loginAlt = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(),'Login')]")));
				loginAlt.click();
			}

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

			// 8️⃣ **Find Search Box & Ensure Clickability**
			WebElement searchBox = wait.until(ExpectedConditions.presenceOfElementLocated(
					By.xpath("//input[contains(@class, 'suggestor-input') and contains(@placeholder, 'Enter keyword')]")
			));
			System.out.println("✅ Found search box.");

			// 9️⃣ **Ensure No Overlapping Elements**
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", searchBox);
			((JavascriptExecutor) driver).executeScript("arguments[0].style.display='block';", searchBox);

			//  🔟 **Click Using JavaScript if Intercepted**
			try {
				searchBox.click();
			} catch (ElementClickInterceptedException e) {
				System.err.println("⚠ Click intercepted! Using JavaScript click.");
				((JavascriptExecutor) driver).executeScript("arguments[0].click();", searchBox);
			}

			// **Enter Search Term**
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
			((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);"); // Scroll to bottom
			System.out.println("📜 Scrolled down to load more jobs.");

			// 12️⃣ **Find "Apply" Buttons with Different XPath Variants**
			String[] applyXPaths = {
					"//button[contains(text(),'Apply')]",  // Common pattern
					"//a[contains(@href, 'apply')]",  // Some job boards use links instead of buttons
					"//div[contains(@class, 'jobTuple')]//button",  // Naukri-specific structure
					"//span[contains(text(),'Apply')]/ancestor::button" // Look for span inside buttons
			};

			WebElement applyButton = null;
			for (String xpath : applyXPaths) {
				try {
					applyButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
					System.out.println("✅ Found 'Apply' button with XPath: " + xpath);
					break;
				} catch (Exception ignored) {
					System.err.println("⚠ No 'Apply' button found using: " + xpath);
				}
			}

			// 13️⃣ **If No Apply Buttons Found, Print Page Source for Debugging**
			if (applyButton == null) {
				System.err.println("❌ 'Apply' button NOT found! Printing page source for debugging...");
				System.out.println(driver.getPageSource());
				driver.quit();
				return;
			}

			// 14️⃣ **Apply to Jobs (Limit 5)**
			List<WebElement> applyButtons = driver.findElements(By.xpath("//button[contains(text(),'Apply')]"));
			int count = 0;
			for (WebElement button : applyButtons) {
				if (count >= 5) break;
				((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
				count++;
				Thread.sleep(2000);
			}

			System.out.println("✅ Successfully applied for " + count + " Java Developer jobs on Naukri.com.");

		} catch (Exception e) {
			System.err.println("🚨 Error during job application: " + e.getMessage());
		} finally {
			driver.quit();
			System.out.println("🚪 Closed browser.");
		}
	}
}
