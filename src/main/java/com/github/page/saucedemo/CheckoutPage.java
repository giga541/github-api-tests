package com.github.page.saucedemo;

import com.zebrunner.carina.webdriver.gui.AbstractPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;

public class CheckoutPage extends AbstractPage {

    @FindBy(xpath = "//input[@id='first-name']")
    private ExtendedWebElement firstName;

    @FindBy(xpath = "//input[@id='last-name']")
    private ExtendedWebElement lastName;

    @FindBy(xpath = "//input[@id='postal-code']")
    private ExtendedWebElement postalCode;

    @FindBy(xpath = "//input[@id='continue']")
    private ExtendedWebElement continueButton;

    @FindBy(xpath = "//button[@id='finish']")
    private ExtendedWebElement finishButton;

    @FindBy(xpath = "//h2")
    private ExtendedWebElement confirmationHeader;

    public CheckoutPage(WebDriver driver) {
        super(driver);
    }

    public CheckoutPage fillInfo(String first, String last, String zip) {
        firstName.type(first);
        lastName.type(last);
        postalCode.type(zip);
        continueButton.click();
        return new CheckoutPage(driver);
    }

    public void finish() {
        finishButton.click();
    }

    public String getConfirmationMessage() {
        return confirmationHeader.getText();
    }
}