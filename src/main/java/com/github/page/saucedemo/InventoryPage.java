package com.github.page.saucedemo;

import com.github.page.saucedemo.components.ProductCard;
import com.zebrunner.carina.webdriver.gui.AbstractPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;

import java.util.List;

public class InventoryPage extends AbstractPage {

    @FindBy(css = "//span[@data-test='shopping-cart-badge']")
    private ExtendedWebElement cartBadge;

    @FindBy(css = "//select[@data-test='product-sort-container']")
    private ExtendedWebElement sortDropdown;

    @FindBy(css = "//a[@data-test='shopping-cart-link']")
    private ExtendedWebElement cartIcon;

    @FindBy(css = ".//div[@data-test='inventory-item']")
    private List<ProductCard> productCards;

    public InventoryPage(WebDriver driver) {
        super(driver);
    }

    public List<ProductCard> getProductCards() {
        return productCards;
    }

    public int getCartBadgeCount() {
        if (!cartBadge.isElementPresent(2)) return 0;
        return Integer.parseInt(cartBadge.getText());
    }

    public void sortBy(String value) {
        // value: "az", "za", etc
        sortDropdown.select(value);
    }

    public CartPage goToCart() {
        cartIcon.click();
        return new CartPage(driver);
    }

    public boolean isOpened() {
        return getCurrentUrl().contains("inventory.html");
    }
}