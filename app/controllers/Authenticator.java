package controllers;

import static play.data.Form.form;
import controllers.Authentication.Login;
import play.mvc.Http.Context;
import play.mvc.Result;

public class Authenticator extends play.mvc.Security.Authenticator {
	
	public Result onUnauthorized(Context ctx) {
        return ok(views.html.login.render(form(Login.class)));
	}
}