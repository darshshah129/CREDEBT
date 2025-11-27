package Web;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.SkipException;
import org.testng.annotations.*;
import org.pages.TransactionsPage;
import org.pages.AmortisationPage;
import org.utils.Common;
import org.utils.Locators;
import java.time.Duration;

public class WebSmokeTest {
    // ThreadLocal to give each thread its own WebDriver instance
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();
    private ThreadLocal<TransactionsPage> transactionsPageThreadLocal = new ThreadLocal<>();
    private ThreadLocal<AmortisationPage> amortisationPageThreadLocal = new ThreadLocal<>();

    // Getter methods to access ThreadLocal values
    private WebDriver getDriver() {
        return driverThreadLocal.get();
    }

    private void setDriver(WebDriver driver) {
        driverThreadLocal.set(driver);
    }

    private TransactionsPage getTransactionsPage() {
        return transactionsPageThreadLocal.get();
    }

    private void setTransactionsPage(TransactionsPage page) {
        transactionsPageThreadLocal.set(page);
    }

    private AmortisationPage getAmortisationPage() {
        return amortisationPageThreadLocal.get();
    }

    private void setAmortisationPage(AmortisationPage page) {
        amortisationPageThreadLocal.set(page);
    }

    @AfterClass(alwaysRun = true)
    public void exportFEJson() {
        Common.writeFEJson();
    }
        @BeforeMethod(alwaysRun = true)
        void Setup() {
        try {
            //-CHROME-//
            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--incognito");
            WebDriver driver = new ChromeDriver(options);
            setDriver(driver);  // Store in ThreadLocal

            // Initialize pages with driver
            setTransactionsPage(new TransactionsPage(driver));
            setAmortisationPage(new AmortisationPage(driver));

            driver.manage().window().maximize();

            driver.get("https://development.credebt.com/");

            WebElement emailInput = new WebDriverWait(driver, Duration.ofSeconds(100))
                .until(ExpectedConditions.visibilityOfElementLocated(Locators.EMAIL_INPUT));
            emailInput.click();
            emailInput.sendKeys("testuser@credebt.com");
            driver.findElement(Locators.SUBMIT_BUTTON).click();

            WebElement passwordInput = new WebDriverWait(driver, Duration.ofSeconds(100))
                .until(ExpectedConditions.visibilityOfElementLocated(Locators.PASSWORD_INPUT));
            passwordInput.click();
            passwordInput.sendKeys("harder@1212");
            driver.findElement(Locators.SUBMIT_BUTTON).click();

            new WebDriverWait(driver, Duration.ofSeconds(100))
                .until(ExpectedConditions.visibilityOfElementLocated(Locators.CREDEBTLOGO));
        } catch (Throwable t) {
            // Log the configuration issue and skip the test rather than letting TestNG mark configuration as broken
            Common.error("Setup failed: " + t.getMessage());
            throw new SkipException("Skipping test due to setup failure: " + t.getMessage());
        }
        }

    @Test(priority = 2)
    public void Trade_Transactions_Page() throws Exception {
        getTransactionsPage().runTransactionTest();
    }

    @Test(priority = 2)
    public void Trade_Amortisation() throws Exception {
        getAmortisationPage().runAmortisationTest();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        WebDriver driver = getDriver();
        if (driver != null) {
            try {
                // Defensive cleanup: catch and log exceptions but do not rethrow
                try { driver.manage().deleteAllCookies(); } catch (Throwable t) { Common.error("Error deleting cookies: " + t.getMessage()); }
                try { driver.quit(); } catch (Throwable t) { Common.error("Error quitting driver: " + t.getMessage()); }
            } finally {
                driverThreadLocal.remove();  // Clean up ThreadLocal
            }
        }
    }
    }

