package rest;

import io.quarkiverse.renarde.Controller;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.ws.rs.Path;

@Path("/")
public class Home extends Controller {

  @CheckedTemplate
  public static class Templates {
    public static native TemplateInstance home();
  }

  @Path("/")
  public TemplateInstance home() {
    return Templates.home();
  }
}
