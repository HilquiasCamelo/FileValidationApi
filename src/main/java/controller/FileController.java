package controller;

import interfaces.impl.ImageValidator;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import model.FileForm;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import services.Filevalidator;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@RequestScoped
@Path("/api")
@Produces(MediaType.TEXT_PLAIN)
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
public class FileController {

    private static final Logger log = LoggerFactory.getLogger(FileController.class);

    @Inject
    Filevalidator filevalidator;

    @Inject
    ImageValidator imagevalidator;

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/files")
    public Response uploadFile(@NotNull @MultipartForm FileForm form) {
        try {
            return imagevalidator.validateAndRespond(form);
        } catch (Exception e) {
            log.error("Error processing the file: " + e.getMessage());
            return imagevalidator.buildErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Error processing the file");
        }
    }
}
