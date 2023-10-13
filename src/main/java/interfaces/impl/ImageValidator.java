package interfaces.impl;

import constantes.ImageConstants;
import interfaces.ValidationStrategy;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

import util.NameFormat;


import javax.enterprise.context.RequestScoped;
import javax.imageio.ImageIO;
import javax.ws.rs.core.Response;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import response.CustomResponse;

@RequestScoped
public class ImageValidator implements ValidationStrategy {
    private static final Logger log = LoggerFactory.getLogger(ValidationStrategy.class);
    String  formatName;
    public boolean validateImage(byte[] base64Image) {
        try {
            int width = getWidth(base64Image);
            int height = getHeight(base64Image);

            if(isValidFormat(base64Image) ) return validateDimensions(width, height);

        } catch (Exception e) {
            log.error("Erro ao validar a imagem: " + e.getMessage());
            return false;
        }
        return false;
    }



    private boolean isValidFormat(byte[] image) {

        if (image != null) {
            formatName =  NameFormat.getFormat(image);
            return ImageConstants.ALLOWED_FORMATS_IMG.contains(formatName);
        }
        return false;
    }


    private int getWidth(byte[] imageBytes) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes)) {
            BufferedImage image = ImageIO.read(inputStream);
            return (image != null) ? image.getWidth() : 0;
        } catch (IOException e) {
            log.error("Error reading image width: " + e.getMessage());
            return 0;
        }
    }



    private int getHeight(byte[] imageBytes) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes)) {
            BufferedImage image = ImageIO.read(inputStream);
            return (image != null) ? image.getHeight() : 0;
        } catch (IOException e) {
            log.error("Error reading image height:" + e.getMessage());
            return 0;
        }
    }



    private boolean validateDimensions(int width, int height) {
        log.info("Verification dimensions");
        log.info("Height: " + height);
        log.info("Width: " + width);
        log.info("Format: " + formatName);
        return width <= ImageConstants.LARGE_WIDTH && height >= ImageConstants.LARGE_HEIGHT;
    }


    @Override
    public Response checkFileValidity(byte[] arquivoBytes) {
        try {
            int width = getWidth(arquivoBytes);
            int height = getHeight(arquivoBytes);

            if (!isValidFormat(arquivoBytes) && !validateDimensions(width, height)) {
                log.error("Invalid format and dimensions");
                return buildResponse(Response.Status.UNSUPPORTED_MEDIA_TYPE, "Invalid format and dimensions");
            } else if (!isValidFormat(arquivoBytes)) {
                log.error("Invalid format");
                return buildResponse(Response.Status.UNSUPPORTED_MEDIA_TYPE, "Invalid format");
            } else if (!validateDimensions(width, height)) {
                log.error("Invalid dimensions");
                return buildResponse(Response.Status.BAD_REQUEST, "Invalid dimensions");
            }

            log.debug("Image is valid");
            return buildResponse(Response.Status.CREATED, "Image is valid");

        } catch (Exception e) {
            log.error("Error while validating the image: " + e.getMessage());
            return buildResponse(Response.Status.INTERNAL_SERVER_ERROR, "Error processing the file: " + e.getMessage());
        }
    }

    private Response buildResponse(Response.Status status, String message) {
        CustomResponse customResponse = new CustomResponse(status.getStatusCode(), message);
        return Response.status(status).entity(customResponse).build();
    }


}
