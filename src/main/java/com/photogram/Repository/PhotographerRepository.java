package com.photogram.Repository;

        import com.photogram.Photographer;
        import org.springframework.data.mongodb.repository.MongoRepository;

public interface PhotographerRepository extends MongoRepository<Photographer, String> {
        public Photographer findByBrukernavn(String brukernavn);

}