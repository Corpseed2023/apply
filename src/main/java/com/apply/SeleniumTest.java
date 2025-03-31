package com.apply;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class SeleniumTest {

    public static void main(String[] args) {
        System.out.println("📢 Launching Chrome test...");
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver(); // Chrome should launch here
        driver.get("https://google.com");
        System.out.println("✅ Chrome launched!");
    }
}
