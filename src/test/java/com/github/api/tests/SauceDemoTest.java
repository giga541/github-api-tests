package com.github.api.tests;

import com.github.page.saucedemo.CartPage;
import com.github.page.saucedemo.CheckoutPage;
import com.github.page.saucedemo.InventoryPage;
import com.github.page.saucedemo.LoginPage;
import com.github.page.saucedemo.components.CartItem;
import com.github.page.saucedemo.components.ProductCard;
import com.zebrunner.carina.core.IAbstractTest;
import com.zebrunner.carina.utils.config.Configuration;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

public class SauceDemoTest implements IAbstractTest {

    private String username;
    private String password;

    @BeforeMethod
    public void setUp() {
        username = Configuration.get("valid_username").orElse("standard_user");
        password = Configuration.get("password").orElse("secret_sauce");
    }

//     Test 1: Login and verify landing on inventory page
    @Test
    public void verifyLogin() {
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.open();

        InventoryPage inventoryPage = loginPage.login(username, password);

        Assert.assertTrue(inventoryPage.isOpened(),
                "Should land on inventory page after login");
    }

    // Test 2: Add product to cart and verify badge count
    @Test
    public void testAddProductToCart() {

        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.open();
        InventoryPage inventoryPage = loginPage.login(username, password);

        Assert.assertEquals(inventoryPage.getCartBadgeCount(), 0,
                "Cart should be empty before adding product");

        List<ProductCard> products = inventoryPage.getProductCards();
        products.get(0).addToCart();

        Assert.assertEquals(inventoryPage.getCartBadgeCount(), 1,
                "Cart badge should show 1 after adding a product");
    }

    // Test 3: Sort products by price low to high
    @Test
    public void testSortByPriceLowToHigh() {
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.open();
        InventoryPage inventoryPage = loginPage.login(username, password);

        inventoryPage.sortBy("lohi");

        List<ProductCard> products = inventoryPage.getProductCards();
        for (int i = 0; i < products.size() - 1; i++) {
            double current = products.get(i).getPrice();
            double next = products.get(i + 1).getPrice();
            Assert.assertTrue(current <= next,
                    "Products should be sorted low to high but found " + current + " before " + next);
        }
    }

    // Test 4: Complete full checkout flow
    @Test
    public void testCompleteCheckoutFlow() {
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.open();
        InventoryPage inventoryPage = loginPage.login(username, password);

        inventoryPage.getProductCards().get(0).addToCart();

        CartPage cartPage = inventoryPage.goToCart();
        CheckoutPage checkoutPage = cartPage.proceedToCheckout();
        CheckoutPage checkoutPageTwo = checkoutPage.fillInfo("John", "Doe", "12345");
        checkoutPageTwo.finish();

        Assert.assertEquals(checkoutPageTwo.getConfirmationMessage(), "Thank you for your order!",
                "Should see order confirmation message");
    }

//     Test 5: Remove item from cart
        @Test
        public void testRemoveItemFromCart() {
            LoginPage loginPage = new LoginPage(getDriver());
            loginPage.open();
            InventoryPage inventoryPage = loginPage.login(username, password);

            inventoryPage.getProductCards().get(0).addToCart();
            CartPage cartPage = inventoryPage.goToCart();

            cartPage.getCartItems().get(0).remove();

            Assert.assertEquals(cartPage.getItemCount(), 0,
                    "Cart should be empty after removing the item");
        Assert.assertFalse(cartPage.isBadgePresent(),
                "Cart badge should not be present when cart is empty");
}
}