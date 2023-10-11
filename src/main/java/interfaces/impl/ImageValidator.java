package interfaces.impl;

import constantes.ImageConstants;
import interfaces.ValidationStrategy;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

import util.NameFormat;


import javax.enterprise.context.RequestScoped;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

@RequestScoped
public class ImageValidator implements ValidationStrategy {
    private static final Logger log = LoggerFactory.getLogger(ValidationStrategy.class);
    String  formatName;
    private boolean isValidFormat(byte[] image) {

        if (image != null) {
            formatName =  NameFormat.getFormat(image);
            return ImageConstants.ALLOWED_FORMATS_IMG.contains(formatName);
        }
        return false;
    }
    public boolean validateImage(byte[] base64Image) {
        try {
            int width = getWidth(base64Image);
            int height = getHeight(base64Image);

            if(isValidFormat(base64Image) ) return validateDimensions(width, height, formatName);

        } catch (Exception e) {
            log.error("Erro ao validar a imagem: " + e.getMessage());
            return false;
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



    private boolean validateDimensions(int width, int height, String formatName) {
        log.info("Verification dimensions");
        log.info("Height: " + height);
        log.info("Width: " + width);
        log.info("Format: " + formatName);
        return width <= ImageConstants.LARGE_WIDTH && height >= ImageConstants.LARGE_HEIGHT;
    }

}
