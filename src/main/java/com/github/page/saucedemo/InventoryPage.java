package com.github.page.saucedemo;

import com.github.page.saucedemo.components.ProductCard;
import com.zebrunner.carina.webdriver.gui.AbstractPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;

import java.util.List;

public class InventoryPage extends AbstractPage {

    @FindBy(xpath = "//span[@data-test='shopping-cart-badge']")
    private ExtendedWebElement cartBadge;

    @FindBy(xpath = "//select[@data-test='product-sort-container']")
    private ExtendedWebElement sortDropdown;

    @FindBy(xpath = "//a[@data-test='shopping-cart-link']")
    private ExtendedWebElement cartIcon;

    @FindBy(xpath = "//div[@data-test='inventory-item']")
    private List<ProductCard> productCards;

    public InventoryPage(WebDriver driver) {
        super(driver);
    }

    public List<ProductCard> getProductCards() {
        return productCards;
    }

    public int getCartBadgeCount() {
        if (!cartBadge.isElementPresent(5)) return 0;
        return Integer.parseInt(cartBadge.getText());
    }

    public void sortBy(String value) {
        new org.openqa.selenium.support.ui.Select(sortDropdown.getElement()).selectByValue(value);
    }

    public CartPage goToCart() {
        cartIcon.click();
        return new CartPage(driver);
    }

    public boolean isOpened() {
        return getCurrentUrl().contains("inventory.html");
    }
}