package com.photogram.Repository;

import com.photogram.POJO.User;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repository for userklassen, via dette repositoryet blir alle fotograferne og brukerne lagt inn i mongodb. Grunnen til at brukerne og
 * fotograferne kan ligge i samme database er at de skilles på rolle
 */

public interface UserRepository extends MongoRepository<User, String> {
    User findByBrukernavn(String brukernavn);

}