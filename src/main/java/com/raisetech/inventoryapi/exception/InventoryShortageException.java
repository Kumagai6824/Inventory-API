package com.raisetech.inventoryapi.exception;

public class InventoryShortageException extends RuntimeException {
    public InventoryShortageException() {
        super();
    }

    public InventoryShortageException(String message, Throwable cause) {
        super(message, cause);
    }

    public InventoryShortageException(String message) {
        super(message);
    }

    public InventoryShortageException(Throwable cause) {
        super(cause);
    }
}
