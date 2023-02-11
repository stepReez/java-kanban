package service;

public class TimeIntersectionsException extends IllegalArgumentException{

    public TimeIntersectionsException() {
    }

    public TimeIntersectionsException(final String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
