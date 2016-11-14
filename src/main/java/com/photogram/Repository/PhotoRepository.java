package com.photogram.Repository;


        import com.photogram.Photo;
        import org.springframework.data.mongodb.repository.MongoRepository;

public interface PhotoRepository extends MongoRepository<Photo, String> {

}