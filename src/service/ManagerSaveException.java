package service;

import java.io.IOException;

public class ManagerSaveException extends RuntimeException {

    public ManagerSaveException() {
    }

    public ManagerSaveException(final String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
