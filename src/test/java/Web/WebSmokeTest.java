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
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import utils.Common;
import utils.JsonDataLoader;
import utils.Locators;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

public class WebSmokeTest {
       private WebDriver driver;
       Map<String, Object> feValues = new LinkedHashMap<>();
    @BeforeTest
    void Setup() throws InterruptedException {
        
        //-CHROME-//
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");
        driver = new ChromeDriver(options);

//        //-FIREFOX-//
//    WebDriverManager.firefoxdriver().setup();
//
//    FirefoxOptions options = new FirefoxOptions();
//    options.addArguments("-private"); // open Firefox in private mode
//
//    WebDriver driver = new FirefoxDriver(options);


        //-EDGE-//
//    WebDriverManager.edgedriver().setup();
//    EdgeOptions options = new EdgeOptions();
//    options.addArguments("--inprivate");
//    WebDriver driver = new EdgeDriver(options);

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

        @Test
        public void counts_Of_Trade_Transactions_Page() throws Exception {
        Common.verifyElementVisible(driver, Locators.TRADE_ICON);
        Common.clickElement(driver, Locators.TRADE_ICON);
        Common.verifyElementVisible(driver, Locators.TRANSACTIONS_ICON);
        Common.clickElement(driver, Locators.TRANSACTIONS_ICON);

        Common.verifyElementVisible(driver,Locators.TRANSACTIONS_TYPE_DROPDOWN);

        Common.verifyElementVisible(driver,Locators.TOTAL_COUNT_TRANSACTION);
        String FE_GET_ALL_ETR_RECORDS = driver.findElement(Locators.TOTAL_COUNT_TRANSACTION).getText();
        Common.log(FE_GET_ALL_ETR_RECORDS);

        Common.selectNgDropdown(driver, Locators.TRANSACTIONS_TYPE_DROPDOWN,"a-ETR");
        Common.verifyElementVisible(driver,Locators.TOTAL_COUNT_TRANSACTION);
        String FE_GET_A_ETR_RECORDS = driver.findElement(Locators.TOTAL_COUNT_TRANSACTION).getText();
        Common.log(FE_GET_A_ETR_RECORDS);
        feValues.put("FE_GET_A_ETR_RECORDS",FE_GET_A_ETR_RECORDS);

        Common.selectNgDropdown(driver, Locators.TRANSACTIONS_TYPE_DROPDOWN,"b-ETR");
        Common.verifyElementVisible(driver,Locators.TOTAL_COUNT_TRANSACTION);
        String FE_GET_B_ETR_RECORDS = driver.findElement(Locators.TOTAL_COUNT_TRANSACTION).getText();
        Common.log(FE_GET_B_ETR_RECORDS);
        feValues.put("FE_GET_B_ETR_RECORDS",FE_GET_B_ETR_RECORDS);

        Common.selectNgDropdown(driver, Locators.TRANSACTIONS_TYPE_DROPDOWN,"c-ETR");
        Common.verifyElementVisible(driver,Locators.TOTAL_COUNT_TRANSACTION);
        String FE_GET_C_ETR_RECORDS = driver.findElement(Locators.TOTAL_COUNT_TRANSACTION).getText();
        Common.log(FE_GET_C_ETR_RECORDS);
        feValues.put("FE_GET_C_ETR_RECORDS",FE_GET_C_ETR_RECORDS);

        Common.selectNgDropdown(driver, Locators.TRANSACTIONS_TYPE_DROPDOWN,"d-ETR");
        Common.verifyElementVisible(driver,Locators.TOTAL_COUNT_TRANSACTION);
        String FE_GET_D_ETR_RECORDS = driver.findElement(Locators.TOTAL_COUNT_TRANSACTION).getText();
        Common.log(FE_GET_D_ETR_RECORDS);
        feValues.put("FE_GET_D_ETR_RECORDS",FE_GET_D_ETR_RECORDS);

        Common.selectNgDropdown(driver, Locators.TRANSACTIONS_TYPE_DROPDOWN,"f-ETR");
        Common.verifyElementVisible(driver,Locators.TOTAL_COUNT_TRANSACTION);
        String FE_GET_F_ETR_RECORDS = driver.findElement(Locators.TOTAL_COUNT_TRANSACTION).getText();
        Common.log(FE_GET_F_ETR_RECORDS);
        feValues.put("FE_GET_F_ETR_RECORDS",FE_GET_F_ETR_RECORDS);

        Common.selectNgDropdown(driver, Locators.TRANSACTIONS_TYPE_DROPDOWN,"Credit Note");
        Common.verifyElementVisible(driver,Locators.TOTAL_COUNT_TRANSACTION);
        String FE_GET_CREDIT_NOTE_RECORDS = driver.findElement(Locators.TOTAL_COUNT_TRANSACTION).getText();
        Common.log(FE_GET_CREDIT_NOTE_RECORDS);
        feValues.put("FE_GET_CREDIT_NOTE_RECORDS",FE_GET_CREDIT_NOTE_RECORDS);

        Common.selectNgDropdown(driver, Locators.TRANSACTIONS_TYPE_DROPDOWN,"Deduction");
        Common.verifyElementVisible(driver,Locators.TOTAL_COUNT_TRANSACTION);
        String FE_GET_DEDUCTION_RECORDS = driver.findElement(Locators.TOTAL_COUNT_TRANSACTION).getText();
        Common.log(FE_GET_DEDUCTION_RECORDS);
        feValues.put("FE_GET_DEDUCTION_RECORDS",FE_GET_DEDUCTION_RECORDS);

        Common.selectNgDropdown(driver, Locators.TRANSACTIONS_TYPE_DROPDOWN,"Deposit");
        Common.verifyElementVisible(driver,Locators.TOTAL_COUNT_TRANSACTION);
        String FE_GET_DEPOSIT_RECORDS = driver.findElement(Locators.TOTAL_COUNT_TRANSACTION).getText();
        Common.log(FE_GET_DEPOSIT_RECORDS);
        feValues.put("FE_GET_DEPOSIT_RECORDS",FE_GET_DEPOSIT_RECORDS);

        Common.selectNgDropdown(driver, Locators.TRANSACTIONS_TYPE_DROPDOWN,"ICP");
        Common.verifyElementVisible(driver,Locators.TOTAL_COUNT_TRANSACTION);
        String FE_GET_ICP_RECORDS = driver.findElement(Locators.TOTAL_COUNT_TRANSACTION).getText();
        Common.log(FE_GET_ICP_RECORDS);
        feValues.put("FE_GET_ICP_RECORDS",FE_GET_ICP_RECORDS);

        Common.selectNgDropdown(driver, Locators.TRANSACTIONS_TYPE_DROPDOWN,"OCPA");
        Common.verifyElementVisible(driver,Locators.TOTAL_COUNT_TRANSACTION);
        String FE_GET_OCPA_RECORDS = driver.findElement(Locators.TOTAL_COUNT_TRANSACTION).getText();
        Common.log(FE_GET_OCPA_RECORDS);
        feValues.put("FE_GET_OCPA_RECORDS",FE_GET_OCPA_RECORDS);

        Common.selectNgDropdown(driver, Locators.TRANSACTIONS_TYPE_DROPDOWN, "IDP");
        Common.verifyElementVisible(driver, Locators.TOTAL_COUNT_TRANSACTION);
        String FE_GET_IDP_RECORDS = driver.findElement(Locators.TOTAL_COUNT_TRANSACTION).getText();
        Common.log(FE_GET_IDP_RECORDS);
        feValues.put("FE_GET_IDP_RECORDS",FE_GET_IDP_RECORDS);

        Common.selectNgDropdown(driver, Locators.TRANSACTIONS_TYPE_DROPDOWN, "ORP");
        Common.verifyElementVisible(driver, Locators.TOTAL_COUNT_TRANSACTION);
        String FE_GET_ORP_RECORDS = driver.findElement(Locators.TOTAL_COUNT_TRANSACTION).getText();
        Common.log(FE_GET_ORP_RECORDS);
        feValues.put("FE_GET_ORP_RECORDS",FE_GET_ORP_RECORDS);

        Common.selectNgDropdown(driver, Locators.TRANSACTIONS_TYPE_DROPDOWN, "ORR");
        Common.verifyElementVisible(driver, Locators.TOTAL_COUNT_TRANSACTION);
        String FE_GET_ORR_RECORDS = driver.findElement(Locators.TOTAL_COUNT_TRANSACTION).getText();
        Common.log(FE_GET_ORR_RECORDS);
        feValues.put("FE_GET_ORR_RECORDS",FE_GET_ORR_RECORDS);

        Common.selectNgDropdown(driver, Locators.TRANSACTIONS_TYPE_DROPDOWN, "RSA");
        Common.verifyElementVisible(driver, Locators.TOTAL_COUNT_TRANSACTION);
        String FE_GET_RSA_RECORDS = driver.findElement(Locators.TOTAL_COUNT_TRANSACTION).getText();
        Common.log(FE_GET_RSA_RECORDS);
        feValues.put("FE_GET_RSA_RECORDS",FE_GET_RSA_RECORDS);

        Common.selectNgDropdown(driver, Locators.TRANSACTIONS_TYPE_DROPDOWN, "OTR");
        Common.verifyElementVisible(driver, Locators.TOTAL_COUNT_TRANSACTION);
        String FE_GET_OTR_RECORDS = driver.findElement(Locators.TOTAL_COUNT_TRANSACTION).getText();
        Common.log(FE_GET_OTR_RECORDS);
        feValues.put("FE_GET_CREDIT_NOTE_RECORDS",FE_GET_CREDIT_NOTE_RECORDS);

        Common.selectNgDropdown(driver, Locators.TRANSACTIONS_TYPE_DROPDOWN, "OPAC");
        Common.verifyElementVisible(driver, Locators.TOTAL_COUNT_TRANSACTION);
        String FE_GET_OPAC_RECORDS = driver.findElement(Locators.TOTAL_COUNT_TRANSACTION).getText();
        Common.log(FE_GET_OPAC_RECORDS);
        feValues.put("FE_GET_OPAC_RECORDS",FE_GET_OPAC_RECORDS);

        Common.selectNgDropdown(driver, Locators.TRANSACTIONS_TYPE_DROPDOWN, "ETR");
        Common.verifyElementVisible(driver, Locators.TOTAL_COUNT_TRANSACTION);
        String FE_GET_ALL_ETR_LIKE_RECORDS = driver.findElement(Locators.TOTAL_COUNT_TRANSACTION).getText();
        Common.log(FE_GET_ALL_ETR_LIKE_RECORDS);
        feValues.put("FE_GET_ALL_ETR_LIKE_RECORDS",FE_GET_ALL_ETR_LIKE_RECORDS);

        Common.selectNgDropdown(driver, Locators.TRANSACTIONS_TYPE_DROPDOWN, "Asset");
        Common.verifyElementVisible(driver, Locators.TOTAL_COUNT_TRANSACTION);
        String FE_GET_ASSET_TYPE_RECORDS = driver.findElement(Locators.TOTAL_COUNT_TRANSACTION).getText();
        Common.log(FE_GET_ASSET_TYPE_RECORDS);
        feValues.put("FE_GET_ASSET_TYPE_RECORDS",FE_GET_ASSET_TYPE_RECORDS);

        Common.selectNgDropdown(driver, Locators.TRANSACTIONS_TYPE_DROPDOWN, "Trade");
        Common.verifyElementVisible(driver, Locators.TOTAL_COUNT_TRANSACTION);
        String FE_GET_TRADE_TYPE_RECORDS = driver.findElement(Locators.TOTAL_COUNT_TRANSACTION).getText();
        Common.log(FE_GET_TRADE_TYPE_RECORDS);
        feValues.put("FE_GET_TRADE_TYPE_RECORDS",FE_GET_TRADE_TYPE_RECORDS);

        Common.selectNgDropdown(driver, Locators.TRANSACTIONS_TYPE_DROPDOWN, "Debits");
        Common.verifyElementVisible(driver, Locators.TOTAL_COUNT_TRANSACTION);
        String FE_GET_DEBITS_TYPE_RECORDS = driver.findElement(Locators.TOTAL_COUNT_TRANSACTION).getText();
        Common.log(FE_GET_DEBITS_TYPE_RECORDS);
        feValues.put("FE_GET_DEBITS_TYPE_RECORDS",FE_GET_DEBITS_TYPE_RECORDS);

        Common.selectNgDropdown(driver, Locators.TRANSACTIONS_TYPE_DROPDOWN, "Credits");
        Common.verifyElementVisible(driver, Locators.TOTAL_COUNT_TRANSACTION);
        String FE_GET_CREDITS_TYPE_RECORDS = driver.findElement(Locators.TOTAL_COUNT_TRANSACTION).getText();
        Common.log(FE_GET_CREDITS_TYPE_RECORDS);
        feValues.put("FE_GET_CREDITS_TYPE_RECORDS",FE_GET_CREDITS_TYPE_RECORDS);


        Common.writeFEJson(feValues);
}

@AfterTest
public void tearDown(){
        driver.manage().deleteAllCookies();
        driver.quit();
}

    
}
