package rest;

import java.util.Set;

import org.hibernate.validator.constraints.Length;
import org.jboss.resteasy.reactive.RestForm;

import io.quarkiverse.renarde.Controller;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import model.Owner;

@Path("/owners")
public class Owners extends Controller {

  @CheckedTemplate
  public static class Templates {
    public static native TemplateInstance register();
    public static native TemplateInstance granted();
  }

  @Path("/register")
  public TemplateInstance registerForm() {
    return Templates.register();
  }

  @Path("/granted")
  public TemplateInstance granted() {
    return Templates.granted();
  }

  @POST
  @Path("/register")
  public void register(
    @RestForm @NotBlank @Length(max = 50) String username,
    @RestForm @Email @NotBlank @Length(max = 255) String email,
    @RestForm @Length(min = 8) String password,
    @RestForm @Length(min = 8) String passwordConfirm,
    @RestForm @Length(max = 2048) String secretText,
    @RestForm Set<String> languages
  ) {
    validation.required("username", username);
    validation.required("email", email);
    validation.required("password", password);
    validation.required("passwordConfirm", passwordConfirm);
    validation.equals("password", password, passwordConfirm);

    if (Owner.findByEmail(email) != null)
      validation.addError("email", "Email already in use");

    if (Owner.findByUsername(username) != null)
      validation.addError("username", "Username already taken");

    if (validationFailed()) registerForm();

    Owner owner = new Owner();
    owner.username = username.trim();
    owner.email = email.trim();
    owner.password = BcryptUtil.bcryptHash(password);
    owner.status = true;
    owner.isAdmin = false;
    owner.secretText = secretText;
    owner.languages = languages;
    owner.persist();

    granted();
  }
}
