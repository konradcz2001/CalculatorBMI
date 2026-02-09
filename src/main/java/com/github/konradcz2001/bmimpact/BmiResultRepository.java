package com.github.konradcz2001.bmimpact;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for accessing BmiResult documents in MongoDB.
 */
@Repository
public interface BmiResultRepository extends MongoRepository<BmiResult, String> {

    /**
     * Finds all BMI results belonging to a specific user.
     *
     * @param username the username of the owner
     * @return list of BMI results
     */
    List<BmiResult> findByUsername(String username);
}