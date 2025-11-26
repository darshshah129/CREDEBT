package Web;

import DB.DatabaseSmokeTest;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;
import org.pages.TransactionsPage;
import org.pages.AmortisationPage;
import org.utils.Common;
import org.utils.Locators;
import java.time.Duration;

public class WebSmokeTest {
    private WebDriver driver;
    private TransactionsPage transactionsPage;
    private AmortisationPage amortisationPage;

    @AfterClass(alwaysRun = true)
    public void exportFEJson() {
        Common.writeFEJson();
    }
    @BeforeMethod(alwaysRun = true)
    void Setup() throws InterruptedException {
        //-CHROME-//
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");
        driver = new ChromeDriver(options);

        // Initialize pages with driver
        transactionsPage = new TransactionsPage(driver);
        amortisationPage = new AmortisationPage(driver);

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
    }

    @Test(priority = 2)
    public void Trade_Transactions_Page() throws Exception {
        transactionsPage.runTransactionTest();
    }

    @Test(priority = 2)
    public void Trade_Amortisation() throws Exception {
        amortisationPage.runAmortisationTest();
    }
    @AfterMethod(alwaysRun = true)
        public void tearDown() {
            driver.manage().deleteAllCookies();
            driver.quit();
        }
    }

