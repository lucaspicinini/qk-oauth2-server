package rest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import org.hibernate.validator.constraints.Length;
import org.jboss.resteasy.reactive.RestForm;

import io.quarkiverse.renarde.router.Router;
import io.quarkiverse.renarde.security.ControllerWithUser;
import io.quarkiverse.renarde.security.RenardeSecurity;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import model.Owner;

@Path("/owners")
public class Owners extends ControllerWithUser<Owner> {
  @Inject
  RenardeSecurity security;

  // Templates
  @CheckedTemplate
  public static class Templates {
    public static native TemplateInstance owners();
    public static native TemplateInstance register();
    public static native TemplateInstance login();
    public static native TemplateInstance dashboard();
    public static native TemplateInstance logout();
  }

  @Path("/")
  public TemplateInstance owners() {
    return Templates.owners();
  }

  @Path("/register")
  public TemplateInstance registerForm() {
    checkLogout();
    return Templates.register();
  }

  @Path("/login")
  public TemplateInstance loginForm() {
    checkLogout();
    return Templates.login();
  }

  @Authenticated
  @Path("/dashboard")
  public TemplateInstance dashboard() {
    return Templates.dashboard();
  }

  @Authenticated
  @Path("/logout")
  public TemplateInstance logoutForm() {
    return Templates.logout();
  }

  // Actions
  @POST
  @Path("/register")
  public void register(
    @RestForm @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers and underscores.")
    @NotBlank(message = "This field is required.") @Length(max = 100) String username,

    @RestForm @Pattern(regexp = "\\d{11}", message = "Phone must contain exactly 11 numeric digits")
    @NotBlank(message = "This field is required.") String phone,

    @RestForm @Pattern(regexp = "^$|^(https?://)?[\\w.-]+(\\.[\\w.-]+)+[/#?]?.*$", message = "Invalid website URL")
    @Length(max = 255) String website,

    @RestForm @NotNull(message = "Birthdate is required.")
    @Past(message = "Birthdate must be in the past.") LocalDate birthdate,

    @RestForm @Email @NotBlank(message = "This field is required.") @Length(max = 255) String email,
    @RestForm @NotBlank(message = "This field is required.") @Length(max = 100) String givenName,
    @RestForm @NotBlank(message = "This field is required.") @Length(max = 100) String familyName,
    @RestForm @Size(min = 8, message = "Password must be at least 8 characters long.") String password,
    @RestForm @Size(min = 8, message = "Password must be at least 8 characters long.") String passwordConfirm,
    @RestForm @Length(max = 2048) String secretText,
    @RestForm Set<String> languages
  ) {
    validation.equals("password", password, passwordConfirm);

    if (Owner.findByPhone(phone) != null)
      validation.addError("phone", "Phone already in use.");

    if (Owner.findByEmail(email) != null)
      validation.addError("email", "Email already in use.");

    if (Owner.findByUsername(username) != null)
      validation.addError("username", "Username already taken.");

    if (validationFailed()) registerForm();

    Owner owner = new Owner();
    owner.username = username.trim();
    owner.phone = phone;
    owner.website = website;
    owner.birthdate = birthdate;
    owner.email = email.trim();
    owner.givenName = givenName.trim();
    owner.familyName = familyName.trim();
    owner.password = BcryptUtil.bcryptHash(password);
    owner.secretText = secretText;
    owner.languages = languages;

    owner.createdAt = LocalDateTime.now();
    owner.updatedAt = LocalDateTime.now();
    owner.status = true;
    owner.isAdmin = false;

    owner.persist();
    flash("success", "User successfully registered.");
    owners();
  }

  @POST
  @Path("/login")
  public Response login(
    @RestForm @Pattern(regexp = "\\d{11}", message = "Phone must contain exactly 11 numeric digits")
    @NotBlank(message = "This field is required.")
    String phone,
    @RestForm @Size(min = 8, message = "Password must be at least 8 characters long.")
    String password
  ) {
    Owner owner = Owner.findByPhone(phone);

    if (owner == null)
      validation.addError("phone", "This phone is not registered.");

    if (owner != null && !BcryptUtil.matches(password, owner.password))
      validation.addError("password", "Invalid password.");

    if (validationFailed()) loginForm();

    NewCookie cookie = security.makeUserCookie(owner);

    return Response.seeOther(Router.getURI(Owners::dashboard))
      .cookie(cookie)
      .build();
  }

  private void checkLogout() {
    if (getUser() != null) {
      flash("logoutFirst", "You have been logged out first.");
      logoutForm();
    }
  }
}
