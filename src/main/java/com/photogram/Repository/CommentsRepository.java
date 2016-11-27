package com.photogram.Repository;


import com.photogram.POJO.Comments;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CommentsRepository extends MongoRepository<Comments, String> {

}