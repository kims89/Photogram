package com.photogram.Repository;


import com.photogram.Comments;
import com.photogram.Photo;
import com.photogram.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CommentsRepository extends MongoRepository<Comments, String> {

}