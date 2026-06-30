package com.github.api.tests;

import com.github.page.saucedemo.CartPage;
import com.github.page.saucedemo.CheckoutPage;
import com.github.page.saucedemo.InventoryPage;
import com.github.page.saucedemo.LoginPage;
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
        username   = Configuration.get("valid_username").orElse("standard_user");
        password   = Configuration.get("password").orElse("secret_sauce");
    }

    // Test 1: Login and verify landing on inventory page
    @Test
    public void verifyLogin() {
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.open();

        InventoryPage inventoryPage = loginPage.login(username, password);

        Assert.assertTrue(inventoryPage.isOpened(),
                "Should land on inventory page after login");
    }
}