package controller;

import interfaces.ValidationStrategy;
import interfaces.impl.ImageValidator;

import io.smallrye.common.constraint.NotNull;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import lombok.extern.log4j.Log4j2;


import model.FileForm;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import services.Filevalidator;
import util.NameFormat;


import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;

import static constantes.ImageConstants.ALLOWED_FORMATS_IMG;

@RequestScoped
@Path("/api")
@Produces(MediaType.TEXT_PLAIN)
@Log4j2
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)


public class ArquivoResource {
       private static final Logger log = LoggerFactory.getLogger(ArquivoResource.class);
    @Inject
    Filevalidator filevalidator;

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/files")
    public Response uploadArquivo(@NotNull @MultipartForm FileForm form) {
        try {

            InputStream inputStream = form.file;

            byte[] arquivoBytes = inputStream.readAllBytes();

            if ((ALLOWED_FORMATS_IMG.contains(NameFormat.getFormat(arquivoBytes)))) {
                filevalidator.setStrategy(new ImageValidator());
            }

            if (filevalidator.validate(arquivoBytes)) {
                log.debug("Image valid");
                return Response.status(Response.Status.CREATED).build();
            } else {
                log.error("Image validation failed");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Invalid image").build();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error processing the file").build();
        }
    }

}
