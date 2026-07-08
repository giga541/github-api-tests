package com.github.page.saucedemo;

import com.github.page.saucedemo.components.CartItem;
import com.zebrunner.carina.webdriver.gui.AbstractPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import java.util.List;

public class CartPage extends AbstractPage {

    @FindBy(xpath = "//div[@data-test=\"cart-list\"]")
    private List<CartItem> cartItems;

    @FindBy(xpath = "//button[@id='checkout']")
    private ExtendedWebElement checkoutButton;

    @FindBy(xpath = "//span[@data-test='shopping-cart-badge']")
    private ExtendedWebElement cartBadge;

    public CartPage(WebDriver driver) {
        super(driver);
    }

    public List<CartItem> getCartItems() { return cartItems; }

    public int getItemCount() { return cartItems.size(); }

    public boolean isBadgePresent() { return cartBadge.isElementPresent(2); }

    public CheckoutPage proceedToCheckout() {
        checkoutButton.click();
        return new CheckoutPage(driver);
    }
}