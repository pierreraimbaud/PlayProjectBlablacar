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
        browser.fill("[name=name]").with("???");
        browser.fill("[name=password]").with("???");
        browser.submit("form");
        // User is logged in and redirected to the index page
        // TODO

        // Logout
        // TODO
    }

    @Test
    public void invalidLogin() {
        browser.goTo(routes.Journeys.journeys().url());
        // Submit an empty form
        // TODO
        // Validation error
        assertThat(browser.pageSource()).contains("???");

        // Submit an invalid form
        browser.fill("[name=name]").with("???");
        browser.fill("[name=password]").with("???");
        browser.submit("form");
        assertThat(browser.pageSource()).contains("???");
    }

}
