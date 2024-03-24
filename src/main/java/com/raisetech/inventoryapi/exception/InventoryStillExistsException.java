package com.raisetech.inventoryapi.exception;

public class InventoryStillExistsException extends RuntimeException {
    public InventoryStillExistsException() {
        super();
    }

    public InventoryStillExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public InventoryStillExistsException(String message) {
        super(message);
    }

    public InventoryStillExistsException(Throwable cause) {
        super(cause);
    }
}
