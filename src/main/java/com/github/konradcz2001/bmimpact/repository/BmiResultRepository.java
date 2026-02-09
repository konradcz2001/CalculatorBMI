package com.github.konradcz2001.bmimpact.repository;

import com.github.konradcz2001.bmimpact.model.BmiResult;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for accessing BmiResult documents in MongoDB.
 */
@Repository
public interface BmiResultRepository extends MongoRepository<BmiResult, String> {
    List<BmiResult> findByUsernameOrderByTimestampDesc(String username);

    // Updated to deleteByUsername since the 'name' field was removed from the entity
    void deleteByUsername(String username);
}