package com.photogram.Repository;


import com.photogram.POJO.Comments;
import org.springframework.data.mongodb.repository.MongoRepository;


/**
 * Repository for kommenteringsklassen, via dette repositoryet blir alle kommenterane lagt inn i mongodb
 */

public interface CommentsRepository extends MongoRepository<Comments, String> {

}