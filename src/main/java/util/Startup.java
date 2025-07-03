package util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;

import io.quarkus.runtime.LaunchMode;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.transaction.Transactional;
import model.Owner;

@ApplicationScoped
public class Startup {
  /**
   * This method is executed at the start of your application
   */

  public void start(@Observes StartupEvent evt) {
    // in DEV mode we seed some data
    if (LaunchMode.current() == LaunchMode.DEVELOPMENT)
      createTestUser();
  }

  @Transactional
  void createTestUser() {
    Owner testUser = new Owner();
    testUser.username = "Gordela";
    testUser.phone = "21973637280";
    testUser.birthdate = LocalDate.of(1991, 7, 16);
    testUser.email = "gordela@gordela.com";
    testUser.givenName = "Lucas";
    testUser.familyName = "Picinini";
    testUser.password = "$2a$12$UFxV6SHv7RifemMX0SVa2eOwv9QBu/SivdgDbDEQfYAbjzp1ilZ/2";
    testUser.secretText = "My Secret.";
    var languages = new HashSet<String>();
    languages.add("portuguese");
    languages.add("mandarin");
    languages.add("english");
    testUser.languages = languages;
    testUser.createdAt = LocalDateTime.now();
    testUser.updatedAt = LocalDateTime.now();
    testUser.status = true;
    testUser.isAdmin = false;
    testUser.persist();
  }
}
