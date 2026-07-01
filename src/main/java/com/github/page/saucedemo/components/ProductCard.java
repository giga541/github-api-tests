package com.github.page.saucedemo.components;

import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;

public class ProductCard extends AbstractUIObject {

    @FindBy(xpath = ".//img")
    private ExtendedWebElement image;

    @FindBy(xpath = ".//div//a//div")
    private ExtendedWebElement nameLink;

    @FindBy(xpath = ".//div[@data-test='inventory-item-price']")
    private ExtendedWebElement price;

    @FindBy(xpath = ".//button[contains(@id,'add-to-cart')]")
    private ExtendedWebElement addToCartButton;

    public ProductCard(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public void addToCart() {
        addToCartButton.click();
    }

    public ExtendedWebElement getImage() {
        return image;
    }

    public ExtendedWebElement getNameLink() {
        return nameLink;
    }

    public double getPrice() {
        return Double.parseDouble(price.getText().replace("$", ""));
    }

    public void clickNameLink() {
        nameLink.click();
    }
}