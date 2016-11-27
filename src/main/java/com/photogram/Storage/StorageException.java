package com.photogram.Storage;

/**
 * Her er upload-klassen. De sørger for at det er mulig å laste opp filer til en mappe kalt upload-dir.
 */

public class StorageException extends RuntimeException {

    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}