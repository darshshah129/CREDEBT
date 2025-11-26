package utils;

import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import org.json.JSONObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Map;

public class Common {

    private static int stepCounter = 1;
    private static String getLocatorName(By locator) {
        try {
            for (Field field : Locators.class.getDeclaredFields()) {
                field.setAccessible(true);
                if (field.get(null) instanceof By) {
                    By value = (By) field.get(null);
                    if (value.equals(locator)) {
                        return field.getName();
                    }
                }
            }
        } catch (Exception ignored) {}
        return "UNKNOWN_ELEMENT";
    }

    @Attachment(value = "{msg}", type = "text/plain")
    public static byte[] attachToAllure(String msg) {
        return msg.getBytes();
    }

    public static void log(String message) {
        Reporter.log("Step " + stepCounter + " :: " + message, true);
        Allure.addAttachment("LOG - Step " + stepCounter, message);
        stepCounter++;   // increment step number
    }
    public static void error(String message) {
        Reporter.log("ERROR: " + message, true);
        Allure.addAttachment("ERROR", message);
    }
    public static void waitForLoaderToDisappear(WebDriver driver) {

        By loader = By.xpath("(//div[@class='ng-tns-c2009170884-0 ng-star-inserted'])[2]");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        long endTime = System.currentTimeMillis() + 30000; // 30 seconds timeout

        while (System.currentTimeMillis() < endTime) {

            try {
                WebElement el = driver.findElement(loader);

                // If visible: highlight it purple
                if (el.isDisplayed()) {
                    ((JavascriptExecutor) driver).executeScript(
                            "arguments[0].style.border='6px solid purple'; " +
                                    "arguments[0].style.backgroundColor='rgba(128,0,128,0.3)';",
                            el
                    );

                    Thread.sleep(200); // small wait before rechecking
                } else {
                    break; // disappears -> exit loop
                }

            } catch (Exception e) {
                break; // element removed from DOM -> done
            }
        }

        // Final Selenium wait (ensures invisibility)
        wait.until(ExpectedConditions.invisibilityOfElementLocated(loader));
    }



    public static void waitForPageToLoad(WebDriver driver) {
        new WebDriverWait(driver, Duration.ofSeconds(60)).until(
                webDriver -> ((JavascriptExecutor) webDriver)
                        .executeScript("return document.readyState").equals("complete"));
    }

    // 🔹 Highlight element in a specific color (green/red)
    public static void highlightElement(WebDriver driver, WebElement element, String color) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].style.border='5px solid " + color + "'", element);
    }

    // 🔹 Verify element is visible + highlight in green + assert isDisplayed()
    public static void verifyElementVisible(WebDriver driver, By locator) {
        String elementName = getLocatorName(locator);
        Common.log("Verify "+elementName+" is visible");
        waitForLoaderToDisappear(driver);
        WebElement element = new WebDriverWait(driver, Duration.ofSeconds(100))
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
        highlightElement(driver, element, "green");
        Common.log(elementName+" is visible");
        // Explicit assertion (like Assert.assertTrue(driver.findElement(...).isDisplayed()))
        Assert.assertTrue(
                element.isDisplayed(),
                " Element is NOT displayed: " + locator.toString()
        );
    }

    // 🔹 Click element (wait until clickable + highlight in red)
    public static void clickElement(WebDriver driver, By locator) {
        try {
            // 1️⃣ Wait until page is fully loaded before trying to click
            waitForPageToLoad(driver);

            String elementName = getLocatorName(locator);
            // 2️⃣ Wait for the element to be clickable
            WebElement element = new WebDriverWait(driver, Duration.ofSeconds(30))
                    .until(ExpectedConditions.elementToBeClickable(locator));

            // 3️⃣ Highlight in red
            highlightElement(driver, element, "red");
            Common.log("Click on " + elementName);
            element.click();
            Thread.sleep(4000);
            waitForLoaderToDisappear(driver);

        } catch (Exception firstException) {
            try {
                Common.log("⚠ First click attempt failed, retrying...");

                // Wait again for page to load and element to become clickable
                waitForPageToLoad(driver);
                WebElement element = new WebDriverWait(driver, Duration.ofSeconds(30))
                        .until(ExpectedConditions.elementToBeClickable(locator));

                highlightElement(driver, element, "red");

                element.click();
                Thread.sleep(3000);
                waitForLoaderToDisappear(driver);

            } catch (Exception secondException) {
                Common.error("❌ Failed to click element after retry: " + locator);
                secondException.printStackTrace();
                throw new RuntimeException("Element not clickable even after retry: " + locator, secondException);
            }
        }
    }
    public static void selectNgDropdown(WebDriver driver, By locator, String optionText) {
        String name = getLocatorName(locator);  // AUTO-DETECT locator variable name

        try {
            waitForPageToLoad(driver);
            Common.log("➡ Selecting '" + optionText + "' from " + name);

            WebElement dropdown = new WebDriverWait(driver, Duration.ofSeconds(30))
                    .until(ExpectedConditions.elementToBeClickable(locator));

            highlightElement(driver, dropdown, "yellow");
            Thread.sleep(800);
            waitForLoaderToDisappear(driver);
            dropdown.click();
            Thread.sleep(2000);
            waitForLoaderToDisappear(driver);

            By option = By.xpath("//div[contains(@class,'ng-option')]//*[normalize-space()='" + optionText + "']");

            WebElement optionElement = new WebDriverWait(driver, Duration.ofSeconds(30))
                    .until(ExpectedConditions.visibilityOfElementLocated(option));

            highlightElement(driver, optionElement, "green");
            optionElement.click();
            Thread.sleep(2000);
            waitForLoaderToDisappear(driver);
            Thread.sleep(5000);
        } catch (Exception first) {
            try {
                Common.log("⚠ First attempt failed, retrying...");

                waitForPageToLoad(driver);
                waitForLoaderToDisappear(driver);
                WebElement dropdown = new WebDriverWait(driver, Duration.ofSeconds(30))
                        .until(ExpectedConditions.elementToBeClickable(locator));
                highlightElement(driver, dropdown, "yellow");
                Thread.sleep(800);
                dropdown.click();
                Thread.sleep(2000);
                waitForLoaderToDisappear(driver);
                Thread.sleep(5000);
                By option = By.xpath("//div[contains(@class,'ng-option')][normalize-space()='" + optionText + "']");
                WebElement optionElement = new WebDriverWait(driver, Duration.ofSeconds(30))
                        .until(ExpectedConditions.visibilityOfElementLocated(option));
                highlightElement(driver, optionElement, "green");
                optionElement.click();
                waitForLoaderToDisappear(driver);
                Thread.sleep(5000);

            } catch (Exception second) {
                throw new RuntimeException("ng-select selection failed: " + name, second);
            }
        }
    }
    // ONE global map for all FE tests
    private static final Map<String, Object> GLOBAL_FE_VALUES =
            new ConcurrentHashMap<>();

    // Called from ANY FE test
    public static void addFEValue(String key, Object value) {
        GLOBAL_FE_VALUES.put(key, value);
    }

    // Called ONCE after all tests
    public static synchronized void writeFEJson() {
        String filePath = System.getProperty("user.dir") + "/StoredData/FE_Values.json";

        try (FileWriter file = new FileWriter(filePath)) {

            file.write("{\n");
            int index = 0;
            int size = GLOBAL_FE_VALUES.size();

            for (Map.Entry<String, Object> entry : GLOBAL_FE_VALUES.entrySet()) {
                file.write("  \"" + entry.getKey() + "\" : \"" + entry.getValue() + "\"");
                if (index < size - 1) file.write(",");
                file.write("\n");
                index++;
            }

            file.write("}");
            log("✅ FE JSON exported successfully");

        } catch (Exception e) {
            log("❌ ERROR exporting FE JSON: " + e.getMessage());
        }
    }
//    public static void writeFEJson(Map<String, Object> feValues) {
//        String filePath = System.getProperty("user.dir") + "/StoredData/FE_Values.json";
//
//        try (FileWriter file = new FileWriter(filePath)) {
//
//            file.write("{\n");
//
//            int index = 0;
//            int size = feValues.size();
//
//            for (Map.Entry<String, Object> entry : feValues.entrySet()) {
//                String key = entry.getKey();
//                Object value = entry.getValue();
//
//                file.write("  \"" + key + "\" : \"" + value + "\"");
//                if (index < size - 1) file.write(",");
//                file.write("\n");
//
//                index++;
//            }
//
//            file.write("}");
//
//            Common.log("FE JSON exported successfully: " + filePath);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            Common.log("ERROR exporting FE JSON: " + e.getMessage());
//        }
//    }
    public JSONObject DB_JSON = null;
    public JSONObject FE_JSON = null;
    public void loadJsonFiles() {
        try {
            if (DB_JSON == null) {
                DB_JSON = new JSONObject(
                        new String(Files.readAllBytes(
                                Paths.get(System.getProperty("user.dir"),"StoredData","db_values.json")
                        ))
                );
            }

            if (FE_JSON == null) {
                FE_JSON = new JSONObject(
                        new String(Files.readAllBytes(
                                Paths.get(System.getProperty("user.dir"),"StoredData","FE_Values.json")
                        ))
                );
            }

        } catch (Exception e) {
            log("❌ Failed to load JSON files: " + e.getMessage());
        }
    }

    public void compareDB_FE(String dbKey, String feKey) {
        loadJsonFiles();

        try {
            // --- DB VALUE ---
            Object dbRaw = DB_JSON.get(dbKey);
            double dbVal = Double.parseDouble(dbRaw.toString());
            dbVal = Math.round(dbVal * 100.0) / 100.0;  // Round to 2 decimals

            // --- FE VALUE ---
            String feRaw = FE_JSON.get(feKey).toString();
            feRaw = feRaw.replace(",", "");             // Remove commas
            double feVal = Double.parseDouble(feRaw);

            // --- COMPARE ---
            if (dbVal == feVal) {
                log("✔ MATCH: " + dbKey + " (DB=" + dbVal + ") == " + feKey + " (FE=" + feVal + ")");
            } else {
                error("❌ MISMATCH → "
                        + dbKey + " vs " + feKey
                        + " | DB: " + dbVal + "  FE: " + feVal);
            }

        } catch (Exception e) {
            error("❌ ERROR comparing " + dbKey + " & " + feKey + ": " + e.getMessage());
        }
    }
    public static void selectAggregateDropdown(WebDriver driver, By locator, String visibleText) {
        String name = getLocatorName(locator);

        try {
            waitForPageToLoad(driver);
            Common.log("➡ Selecting '" + visibleText + "' from " + name);

            WebElement dropdown = new WebDriverWait(driver, Duration.ofSeconds(20))
                    .until(ExpectedConditions.elementToBeClickable(locator));

            highlightElement(driver, dropdown, "yellow");

            dropdown.click();
            Thread.sleep(800);

            By option = By.xpath("//option[normalize-space()='" + visibleText + "']");

            WebElement optionElement = new WebDriverWait(driver, Duration.ofSeconds(20))
                    .until(ExpectedConditions.visibilityOfElementLocated(option));

            highlightElement(driver, optionElement, "green");

            optionElement.click();
            Thread.sleep(1200);

            Common.log("✔ Selected: " + visibleText);

        } catch (Exception e1) {
            Common.log("⚠ First attempt failed, retrying...");

            try {
                WebElement dropdown = new WebDriverWait(driver, Duration.ofSeconds(20))
                        .until(ExpectedConditions.elementToBeClickable(locator));

                highlightElement(driver, dropdown, "yellow");
                dropdown.click();
                Thread.sleep(800);

                By option = By.xpath("//option[normalize-space()='" + visibleText + "']");
                WebElement optionElement = new WebDriverWait(driver, Duration.ofSeconds(20))
                        .until(ExpectedConditions.visibilityOfElementLocated(option));

                highlightElement(driver, optionElement, "green");
                optionElement.click();
                Thread.sleep(1200);

                Common.log("✔ Selected after retry: " + visibleText);

            } catch (Exception e2) {
                throw new RuntimeException("Dropdown <select> selection failed for: " + name, e2);
            }
        }
    }

}
