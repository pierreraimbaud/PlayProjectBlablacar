package controllers;

import play.data.Form;
import play.data.validation.Constraints.*;
import play.data.validation.ValidationError;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.login;

import java.util.ArrayList;
import java.util.List;

import static play.data.Form.form;

/**
 * Controller grouping actions related to authentication
 */
public class Authentication extends Controller {
    /**
     * Show the authentication form
     */
    public static Result login() {
        return ok(views.html.login.render(form(Login.class)));
    }

    /**
     * Handle the authentication form submission.
     *
     * If the submitted data is invalid (e.g. the user password is wrong), this action must return a 400 status code
     * and show again the form with its errors.
     *
     * Otherwise, the user must be authenticated (his user id should be stored into his session) and redirected to the index page.
     */
    public static Result authenticate() {
        // Read the data of the form submission
        Form<Login> loginForm = form(Login.class).bindFromRequest();
        
        if (loginForm.hasErrors()) {
        	// Failure -> reply with a 400 status code (Bad Request) and show the form with the validation errors
        	return badRequest(login.render(loginForm));
        } else {
        	// associate the user’s name to the "username" key in his session
        	session().clear();
        	session("username", loginForm.get().name);
        	
        	//redirect to the Journeys.journeys action
        	return redirect(routes.Journeys.journeys());        	
        }
    }

    /**
     * Logs out an user (remove his name from his session) and show a good bye message
     */
    public static Result logout() {
    	session().clear();
    	return ok(views.html.logout.render());
    }

    /**
     * @return The current user name
     */
    public static String username() {
        return session("username");
    }

    /**
     * Map the data of the login form submission.
     *
     * Example of use:
     *
     * <pre>
     *     Form<Login> submission = form(Login.class).bindFromRequest();
     * </pre>
     */
    public static class Login {

    	@Required
        public String name;
    	@MinLength(value = 4)
        public String password;

        // If needed, override this method to add a “global” validation rule (i.e not related to a particular field)
        public List<ValidationError> validate() {
        	List<ValidationError> errors = new ArrayList<ValidationError>();
        	
        	if (!(name.equals("admin") && password.equals("adminMDP"))) {
        		errors.add(new ValidationError("name", "Invalid username or password"));
        		errors.add(new ValidationError("password", "Invalid username or password"));
        	}
        	
            return errors.isEmpty() ? null : errors;
        }

    }
}