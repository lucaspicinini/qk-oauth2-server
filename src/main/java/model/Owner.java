package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import io.quarkiverse.renarde.security.RenardeUser;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "owners")
public class Owner extends PanacheEntityBase implements RenardeUser {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  public String id;

  @Column(nullable = false, unique = true, length = 100)
  @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers and underscores")
  @NotBlank
  public String username;

  @Column(nullable = false, unique = true, length = 11)
  @Pattern(regexp = "\\d{11}", message = "Phone must contain exactly 11 numeric digits")
  @NotBlank
  public String phone;

  @Column(length = 255)
  @Pattern(regexp = "^(https?://)?[\\w.-]+(\\.[\\w.-]+)+[/#?]?.*$", message = "Invalid website URL")
  public String website;

  @Column(nullable = false)
  @Past(message = "Birthdate must be in the past")
  public LocalDate birthdate;

  @Column(nullable = false, unique = true, length = 255)
  @Email
  @NotBlank
  public String email;

  @Column(nullable = false, length = 100, name = "given_name")
  @NotBlank
  public String givenName;

  @Column(nullable = false, length = 100, name = "family_name")
  @NotBlank
  public String familyName;

  @Column(nullable = false)
  @Size(min = 8)
  public String password;

  @Column(length = 2048, name = "secret_text")
  public String secretText;

  public Set<String> languages;

  @Column(nullable = false, name = "created_at")
  @PastOrPresent(message = "Date must not be in the future")
  public LocalDateTime createdAt;

  @Column(nullable = false, name = "updated_at")
  @PastOrPresent(message = "Date must not be in the future")
  public LocalDateTime updatedAt;

  @Column(nullable = false, name = "is_admin")
  public boolean isAdmin;

  @Column(nullable = false)
  public boolean status;

  @Override
  public String userId() {
    return email;
  }

  @Override
  public boolean registered() {
    return status;
  }

  @Override
  public Set<String> roles() {
    Set<String> roles = new HashSet<>();
    if (isAdmin)
      roles.add("admin");

    if (!isAdmin)
      roles.add("user");

    return roles;
  }

  public static Owner findByEmail(String email) {
    return find("LOWER(email) = ?1", email.toLowerCase()).firstResult();
  }

  public static Owner findByUsername(String username) {
    return find("LOWER(username) = ?1", username.toLowerCase()).firstResult();
  }
}
