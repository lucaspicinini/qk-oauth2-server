package model;

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
  @Column(nullable = false, unique = true, length = 50)
  public String username;
  @Column(nullable = false, unique = true, length = 255)
  public String email;
  public String password;
  @Column(length = 2048)
  public String secretText;
  public Set<String> languages;
  public boolean isAdmin;
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
      roles.add("owner");

    return roles;
  }

  public static Owner findByEmail(String email) {
    return find("LOWER(email) = ?1", email.toLowerCase()).firstResult();
  }

  public static Owner findByUsername(String username) {
    return find("LOWER(username) = ?1", username.toLowerCase()).firstResult();
  }
}
