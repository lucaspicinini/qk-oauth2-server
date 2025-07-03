package rest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import org.hibernate.validator.constraints.Length;
import org.jboss.resteasy.reactive.RestForm;

import io.quarkiverse.renarde.Controller;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import model.Owner;

@Path("/owners")
public class Owners extends Controller {

  // Templates
  @CheckedTemplate
  public static class Templates {
    public static native TemplateInstance owners();
    public static native TemplateInstance dashboard();
    public static native TemplateInstance register();
  }

  @Path("/")
  public TemplateInstance owners() {
    return Templates.owners();
  }

  @Path("/dashboard")
  public TemplateInstance dashboard() {
    return Templates.dashboard();
  }

  @Path("/register")
  public TemplateInstance registerForm() {
    return Templates.register();
  }

  // Actions
  @POST
  @Path("/register")
  public void register(
    @RestForm @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers and underscores")
    @NotBlank @Length(max = 100) String username,

    @RestForm @Pattern(regexp = "\\d{11}", message = "Phone must contain exactly 11 numeric digits")
    @NotBlank String phone,

    @RestForm @Pattern(regexp = "^$|^(https?://)?[\\w.-]+(\\.[\\w.-]+)+[/#?]?.*$", message = "Invalid website URL")
    @Length(max = 255) String website,

    @RestForm @NotNull(message = "Birthdate is required")
    @Past(message = "Birthdate must be in the past") LocalDate birthdate,

    @RestForm @Email @NotBlank @Length(max = 255) String email,
    @RestForm @NotBlank @Length(max = 100) String givenName,
    @RestForm @NotBlank @Length(max = 100) String familyName,
    @RestForm @Size(min = 8) String password,
    @RestForm @Size(min = 8) String passwordConfirm,
    @RestForm @Length(max = 2048) String secretText,
    @RestForm Set<String> languages
  ) {
    validation.equals("password", password, passwordConfirm);

    if (Owner.findByEmail(email) != null)
      validation.addError("email", "Email already in use");

    if (Owner.findByUsername(username) != null)
      validation.addError("username", "Username already taken");

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

    owners();
  }
}
