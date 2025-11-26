package org.pages;

import org.openqa.selenium.WebDriver;
import org.utils.Common;
import org.utils.Locators;

/**
 * AmortisationPage - Page Object for Trade Amortisation functionality
 * Contains all test logic for amortisation
 */
public class AmortisationPage {
    private WebDriver driver;

    public AmortisationPage(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * Execute complete amortisation test workflow:
     * 1. Navigate to amortisation page
     * 2. Extract all amortisation type counts
     */
    public void runAmortisationTest() throws Exception {
        // Navigate to Amortisation page
        Common.verifyElementVisible(driver, Locators.TRADE_ICON);
        Common.clickElement(driver, Locators.TRADE_ICON);
        Common.verifyElementVisible(driver, Locators.AMORTISATION_ICON);
        Common.clickElement(driver, Locators.AMORTISATION_ICON);
        Thread.sleep(5000);

        // Extract a-ETR amortisation count
        Common.selectNgDropdown(driver, Locators.AMORTISATION_TYPE_DROPDOWN, "a-ETR");
        Common.verifyElementVisible(driver, Locators.AMORTISATION_COUNT);
        String FE_A_ETR_AMORTISATION = driver.findElement(Locators.AMORTISATION_COUNT).getText();
        Common.log(FE_A_ETR_AMORTISATION);
        Common.addFEValue("FE_A_ETR_AMORTISATION", FE_A_ETR_AMORTISATION);

        // Extract f-ETR amortisation count
        Common.clickElement(driver, Locators.AMORTISATION_TYPE_DROPDOWN);
        Common.selectNgDropdown(driver, Locators.AMORTISATION_TYPE_DROPDOWN, "f-ETR");
        Common.verifyElementVisible(driver, Locators.AMORTISATION_COUNT);
        String FE_F_ETR_AMORTISATION = driver.findElement(Locators.AMORTISATION_COUNT).getText();
        Common.log(FE_F_ETR_AMORTISATION);
        Common.addFEValue("FE_F_ETR_AMORTISATION", FE_F_ETR_AMORTISATION);

        // Extract All amortisation count
        Common.clickElement(driver, Locators.AMORTISATION_TYPE_DROPDOWN);
        Common.selectNgDropdown(driver, Locators.AMORTISATION_TYPE_DROPDOWN, "All");
        Common.verifyElementVisible(driver, Locators.AMORTISATION_COUNT);
        String FE_ALL_ETR_AMORTISATION = driver.findElement(Locators.AMORTISATION_COUNT).getText();
        Common.log(FE_ALL_ETR_AMORTISATION);
        Common.addFEValue("FE_ALL_ETR_AMORTISATION", FE_ALL_ETR_AMORTISATION);
    }
}
