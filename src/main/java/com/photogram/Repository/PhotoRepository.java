package com.photogram.Repository;


        import com.photogram.POJO.Photo;
        import org.springframework.data.mongodb.repository.MongoRepository;

        import java.util.List;

public interface PhotoRepository extends MongoRepository<Photo, String> {
        List<Photo> findByphotographerID(String photographerID);

}