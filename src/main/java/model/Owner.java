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

@Entity
@Table(name = "owners")
public class Owner extends PanacheEntityBase implements RenardeUser {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  public String id;

  @Column(nullable = false, unique = true, length = 100)
  public String username;

  @Column(nullable = false, unique = true, length = 11)
  public String phone;

  @Column(length = 255)
  public String website;

  @Column(nullable = false)
  public LocalDate birthdate;

  @Column(nullable = false, unique = true, length = 255)
  public String email;

  @Column(nullable = false, length = 100, name = "given_name")
  public String givenName;

  @Column(nullable = false, length = 100, name = "family_name")
  public String familyName;

  @Column(nullable = false)
  public String password;

  @Column(length = 2048, name = "secret_text")
  public String secretText;

  public Set<String> languages;

  @Column(nullable = false, name = "created_at")
  public LocalDateTime createdAt;

  @Column(nullable = false, name = "updated_at")
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
