package com.photogram.Repository;


        import com.photogram.Photo;
        import org.springframework.data.mongodb.repository.MongoRepository;

        import java.util.List;

public interface PhotoRepository extends MongoRepository<Photo, String> {
        public List<Photo> findAll();

}