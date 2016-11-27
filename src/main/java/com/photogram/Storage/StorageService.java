package com.photogram.Storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Her er upload-klassen. De sørger for at det er mulig å laste opp filer til en mappe kalt upload-dir.
 * Dette interfacet for alle funksjonene som er tilgjengeliggjort i for opplasting av bilder.
 */
public interface StorageService {

    void init();

    void store(MultipartFile file);

    Stream<Path> loadAll();

    Path load(String filename);

    Resource loadAsResource(String filename);

    void deleteAll();

}