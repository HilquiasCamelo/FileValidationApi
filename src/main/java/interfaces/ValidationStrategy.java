package interfaces;

import javax.ws.rs.core.Response;

public interface ValidationStrategy {
    boolean validateImage(byte[]  form);
    Response checkFileValidity(byte[] arquivoBytes);
}