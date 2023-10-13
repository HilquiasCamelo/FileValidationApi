package interfaces.impl;

import constantes.ImageConstants;
import interfaces.ValidationStrategy;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import lombok.SneakyThrows;
import model.FileForm;
import response.CustomResponse;
import util.NameFormat;

import javax.enterprise.context.RequestScoped;
import javax.imageio.ImageIO;
import javax.ws.rs.core.Response;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;

import static constantes.ImageConstants.ALLOWED_FORMATS_IMG;
import static util.NameFormat.getFormat;

@RequestScoped
public class ImageValidator implements ValidationStrategy {
    private static final Logger log = LoggerFactory.getLogger(ValidationStrategy.class);
    String formatName;
    public boolean validateImage(byte[] base64Image) {
        try {
            int width = getImageProperty(base64Image, BufferedImage::getWidth, "width");
            int height = getImageProperty(base64Image, BufferedImage::getHeight, "height");

            return isValidFormat(base64Image) && validateDimensions(width, height);

        } catch (Exception e) {
            log.error("Error validating the image: {}");
            return false;
        }
    }

    private boolean isValidFormat(byte[] image) {
        formatName = getFormat(image);
        return formatName != null && ALLOWED_FORMATS_IMG.contains(formatName);
    }

    private int getImageProperty(byte[] imageBytes, Function<BufferedImage, Integer> propertyGetter, String propertyName) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes)) {
            BufferedImage image = ImageIO.read(inputStream);
            return (image != null) ? propertyGetter.apply(image) : 0;
        } catch (IOException e) {
            log.error("Error reading image {}: {}");
            return 0;
        }
    }

    private boolean validateDimensions(int width, int height) {
        log.info("Verifying dimensions");
        log.info("Height: " + height);
        log.info("Width: " + width);
        log.info("tipyImage: " + formatName);
        return width <= ImageConstants.LARGE_WIDTH && height >= ImageConstants.LARGE_HEIGHT;
    }

    @Override
    public Response checkFileValidity(byte[] arquivoBytes) {
        try {
            int width = getImageProperty(arquivoBytes, BufferedImage::getWidth, "width");
            int height = getImageProperty(arquivoBytes, BufferedImage::getHeight, "height");

            if (!isValidFormat(arquivoBytes) || !validateDimensions(width, height)) {
                return buildResponse(Response.Status.BAD_REQUEST, "Invalid format or dimensions");
            }

            log.debug("Image is valid");
            return buildResponse(Response.Status.CREATED, "Image is valid");

        } catch (Exception e) {
            log.error("Error while validating the image: {}");
            return buildResponse(Response.Status.INTERNAL_SERVER_ERROR, "Error processing the file: " + e.getMessage());
        }
    }

    private Response buildResponse(Response.Status status, String message) {
        CustomResponse customResponse = new CustomResponse(status.getStatusCode(), message);
        return Response.status(status).entity(customResponse).build();
    }

    @SneakyThrows
    public Response validateAndRespond(FileForm form) {
        try {
            if (form.getFile() == null) {
                return buildErrorResponse(Response.Status.BAD_REQUEST, "No file uploaded");
            }

            InputStream inputStream = form.getFile();
            byte[] arquivoBytes = inputStream.readAllBytes(); // Lê os bytes do InputStream
            String fileFormat = NameFormat.getFormat(arquivoBytes);

            if (ALLOWED_FORMATS_IMG.contains(fileFormat)) {
                return checkFileValidity(arquivoBytes);
            } else {
                return buildErrorResponse(Response.Status.BAD_REQUEST, "Unsupported file format");
            }
        } catch (IOException e) {
            log.error("Error processing the file: {}");
            return buildErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Error processing the file");
        }
    }


    public Response buildErrorResponse(Response.Status status, String message) {
        CustomResponse customResponse = new CustomResponse(status.getStatusCode(), message);
        return Response.status(status).entity(customResponse).build();
    }
}
