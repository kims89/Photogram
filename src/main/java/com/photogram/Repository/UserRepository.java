package com.photogram.Repository;

        import com.photogram.User;
        import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
        User findByBrukernavn(String brukernavn);

}