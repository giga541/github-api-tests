package com.github.page.saucedemo.components;

import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;

public class CartItem extends AbstractUIObject {

    @FindBy(css = ".//div[@data-test='inventory-item-name']")
    private ExtendedWebElement name;

    @FindBy(xpath = ".//div[@data-test='inventory-item-price']")
    private ExtendedWebElement price;

    @FindBy(xpath = ".//button[contains(@data-test,'remove')]")
    private ExtendedWebElement removeButton;

    public CartItem(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public String getName() {
        return name.getText();
    }

    public void remove() {
        removeButton.click();
    }
}