package com.github.page.saucedemo;

import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

public class LoginPage extends AbstractPage {

    @FindBy(xpath = "//input[@id='user-name']")
    private ExtendedWebElement usernameField;

    @FindBy(xpath = "//input[@id='password']")
    private ExtendedWebElement passwordField;

    @FindBy(xpath = "//input[@id='login-button']")
    private ExtendedWebElement loginButton;

    @FindBy(xpath = "//h3[@data-test='error']")
    private ExtendedWebElement errorMessage;

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public InventoryPage login(String username, String password) {
        usernameField.type(username);
        passwordField.type(password);
        loginButton.click();
        return new InventoryPage(driver);
    }

    public boolean isErrorDisplayed() {
        return errorMessage.isElementPresent();
    }

    public void open(String baseUrl) {
    }
}
