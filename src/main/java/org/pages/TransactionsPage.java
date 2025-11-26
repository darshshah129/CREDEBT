package org.pages;

import org.openqa.selenium.WebDriver;
import org.utils.Common;
import org.utils.Locators;

/**
 * TransactionsPage - Page Object for Trade Transactions functionality
 * Contains all test logic for transactions
 */
public class TransactionsPage {
    private WebDriver driver;

    public TransactionsPage(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * Execute complete transaction test workflow:
     * 1. Navigate to transactions page
     * 2. Extract all transaction type counts
     * 3. Test aggregate functions
     */
    public void runTransactionTest() throws Exception {
        // Navigate to Transactions page
        Common.verifyElementVisible(driver, Locators.TRADE_ICON);
        Common.clickElement(driver, Locators.TRADE_ICON);
        Common.verifyElementVisible(driver, Locators.TRANSACTIONS_ICON);
        Common.clickElement(driver, Locators.TRANSACTIONS_ICON);
        Thread.sleep(5000);

        // Verify page elements
        Common.verifyElementVisible(driver, Locators.TRANSACTIONS_TYPE_DROPDOWN);
        Common.verifyElementVisible(driver, Locators.TOTAL_COUNT_TRANSACTION);
        String FE_GET_ALL_ETR_RECORDS = driver.findElement(Locators.TOTAL_COUNT_TRANSACTION).getText();
        Common.log(FE_GET_ALL_ETR_RECORDS);

        // Extract all transaction types
        String[][] transactionTypes = {
            {"a-ETR", "FE_GET_A_ETR_RECORDS"},
            {"b-ETR", "FE_GET_B_ETR_RECORDS"},
            {"c-ETR", "FE_GET_C_ETR_RECORDS"},
            {"d-ETR", "FE_GET_D_ETR_RECORDS"},
            {"f-ETR", "FE_GET_F_ETR_RECORDS"},
            {"Credit Note", "FE_GET_CREDIT_NOTE_RECORDS"},
            {"Deduction", "FE_GET_DEDUCTION_RECORDS"},
            {"Deposit", "FE_GET_DEPOSIT_RECORDS"},
            {"ICP", "FE_GET_ICP_RECORDS"},
            {"OCPA", "FE_GET_OCPA_RECORDS"},
            {"IDP", "FE_GET_IDP_RECORDS"},
            {"ORP", "FE_GET_ORP_RECORDS"},
            {"ORR", "FE_GET_ORR_RECORDS"},
            {"RSA", "FE_GET_RSA_RECORDS"},
            {"OTR", "FE_GET_OTR_RECORDS"},
            {"OPAC", "FE_GET_OPAC_RECORDS"},
            {"ETR", "FE_GET_ALL_ETR_LIKE_RECORDS"},
            {"Asset", "FE_GET_ASSET_TYPE_RECORDS"},
            {"Trade", "FE_GET_TRADE_TYPE_RECORDS"},
            {"Debits", "FE_GET_DEBITS_TYPE_RECORDS"},
            {"Credits", "FE_GET_CREDITS_TYPE_RECORDS"}
        };

        for (String[] typeInfo : transactionTypes) {
            String transactionType = typeInfo[0];
            String keyName = typeInfo[1];
            Common.selectNgDropdown(driver, Locators.TRANSACTIONS_TYPE_DROPDOWN, transactionType);
            Common.verifyElementVisible(driver, Locators.TOTAL_COUNT_TRANSACTION);
            String count = driver.findElement(Locators.TOTAL_COUNT_TRANSACTION).getText();
            Common.log(count);
            Common.addFEValue(keyName, count);
        }

        // Test aggregate functions
        Common.selectAggregateDropdown(driver, Locators.TRANSACTION_FUNCTION_SELECTOR, "Sum");
        Thread.sleep(5000);
        Common.selectAggregateDropdown(driver, Locators.TRANSACTION_FUNCTION_SELECTOR, "Average");
        Thread.sleep(5000);
        Common.selectAggregateDropdown(driver, Locators.TRANSACTION_FUNCTION_SELECTOR, "Maximum");
        Thread.sleep(5000);
        Common.selectAggregateDropdown(driver, Locators.TRANSACTION_FUNCTION_SELECTOR, "Minimum");
    }
}
