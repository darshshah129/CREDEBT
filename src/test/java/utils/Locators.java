package utils;

import org.openqa.selenium.By;

public final class Locators {
    public Locators() {}
    public static By EMAIL_INPUT = By.xpath("//input[@type='email']");
    public static By SUBMIT_BUTTON = By.xpath("//input[@type='submit']");
    public static By PASSWORD_INPUT = By.xpath("//input[@placeholder='Password']");
    public static By CREDEBTLOGO = By.xpath("(//img[@alt='homepage'])[1]");
    public static By RESULTSBUTTON = By.xpath("//span[normalize-space(text())='Results']");
    public static By CLOSINGPOSITIONSBUTTON = By.xpath("//span[normalize-space(text())='Closing Positions']");

    //CLOSING//

    public static By CLOSING_HEADING = By.xpath("//p[@title='Fx, Yield, TaR & Closing Bank']");
    public static By UPDATE_BUTTON = By.xpath("//button[normalize-space(text())='Update']");
    public static By ALL_BUTTON = By.xpath("//button[normalize-space(text())='All']");
    public static By MONTH_BUTTON = By.xpath("//button[normalize-space(text())='Month']");
    public static By YEAR_BUTTON = By.xpath("//button[normalize-space(text())='Year']");
    public static By RANGE_BUTTON = By.xpath("//button[normalize-space(text())='Range']");
    public static By PER_PAGE_DROPDOWN = By.xpath("//select[@formcontrolname='perPage']");

    //BANK-BALANCES//

    public static By BANK_BALANCES_TAB = By.xpath("//span[normalize-space(text())='Bank Balances']");
    public static By CLOSING_BANK_BALANCES_TITLE = By.xpath("//p[@title='Closing Bank Balances']");

    //CHARTS//

    public static By CHARTS_TAB = By.xpath("//span[normalize-space()='Charts']");
    public static By CHARTS_CARD_TITLE = By.xpath("//p[@class='card-title ps-1 pe-3']");

    // ENTITIES //

    public static By ENTITIES_TAB = By.xpath("//span[normalize-space()='Entities']");
    public static By ENTITIES_TITLE = By.xpath("//p[@title='Entities']");
    public static By ADD_ENTITY_BUTTON = By.xpath("//button[normalize-space()='Add Entity']");
    public static By ROLE_LABEL = By.xpath("//label[normalize-space()='Role:']");

    // Trade Transaction//
    public static By TRADE_ICON = By.xpath("(//img[@alt='Trade icon']/following-sibling::span)[1]");
    public static By TRANSACTIONS_ICON = By.xpath("//a[@ng-reflect-router-link='tte/transactions']//span[1]");
    public static By TRANSACTIONS_TYPE_DROPDOWN = By.xpath("(//div[@class='ng-select-container ng-has-value'])[1]");
    public static By TOTAL_COUNT_TRANSACTION = By.xpath("(//span[@id='ag-700-row-count'])[1]");

}