package com.photogram.Storage;

/**
 * Her er upload-klassen. De sørger for at det er mulig å laste opp filer til en mappe kalt upload-dir.
 */

public class StorageFileNotFoundException extends StorageException {

    public StorageFileNotFoundException(String message) {
        super(message);
    }

    public StorageFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}