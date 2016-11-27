package com.photogram.Repository;


import com.photogram.POJO.Photo;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Repository for photoklassen, via dette repositoryet blir alle dataene for bildet lagt inn i mongodb. Selve bildet legges i et katalog ved navn upload-dir
 * Det er også lagt til en funksjon slik at man kan finne alle bildene som har alle fotografer.
 */

public interface PhotoRepository extends MongoRepository<Photo, String> {
    List<Photo> findByphotographerID(String photographerID);

}