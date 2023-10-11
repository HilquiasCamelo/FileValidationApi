package response;

public class CustomResponse {
    private int statusCode;
    private String message;
    private String error;

    public CustomResponse(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public CustomResponse(int statusCode, String message, String error) {
        this.statusCode = statusCode;
        this.message = message;
        this.error = error;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public String getError() {
        return error;
    }

    @Override
    public String toString() {
        return "{" +
                "statusCode=" + statusCode +
                ", message='" + message + '\'' +
                '}';
    }
}
