package controllers;

import org.junit.Test;
import play.test.WithBrowser;

import static org.fest.assertions.Assertions.assertThat;

public class AuthenticationTest extends WithBrowser {

    @Test
    public void signingUser() {
        // Try to go to the index page
        browser.goTo(routes.Journeys.journeys().url());
        // User is redirected to the login page
        assertThat(browser.url()).isEqualTo(routes.Authentication.login().url());

        // Fill the signing in form
        browser.fill("[name=name]").with("admin");
        browser.fill("[name=password]").with("adminMDP");
        browser.submit("form");
        // User is logged in and redirected to the index page
        assertThat(browser.url()).isEqualTo(routes.Journeys.journeys().url());
        // Logout
        browser.goTo(routes.Authentication.logout().url());
	browser.goTo(routes.Journeys.journeys().url());
	assertThat(browser.url()).isEqualTo(routes.Authentication.login().url());
    }


    @Test
    public void invalidLogin() {
        browser.goTo(routes.Journeys.journeys().url());
        // Submit an empty form
        browser.fill("[name=name]").with("");
        browser.fill("[name=password]").with("adminMDP");
        browser.submit("form");
	// Validation error
        assertThat(browser.pageSource()).contains("This fill is required");

        // Submit an invalid form
        browser.fill("[name=name]").with("toto");
        browser.fill("[name=password]").with("aaaa");
        browser.submit("form");
        assertThat(browser.pageSource()).contains("Invalid username or password");

// Submit an invalid form (short password)
        browser.fill("[name=name]").with("admin");
        browser.fill("[name=password]").with("mdp");
        browser.submit("form");
        assertThat(browser.pageSource()).contains("Minimum length is 4");
    }
}