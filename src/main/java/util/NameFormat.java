package util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import javax.imageio.ImageIO;

public class NameFormat {
    private NameFormat() {
        throw new IllegalStateException("Utility class");
    }
    public static String getFormat(byte[] imageBytes) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes)) {
            String[] formatNames = ImageIO.getReaderFormatNames();

            Optional<String> format = Arrays.stream(formatNames)
                    .filter(f -> ImageIO.getImageReadersByFormatName(f).hasNext())
                    .findFirst();

            return format.map(String::toLowerCase).orElse("Unknown format");
        } catch (IOException e) {
            e.printStackTrace();
            return "Error reading image";
        }
    }
}

