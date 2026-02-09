package com.github.konradcz2001.bmimpact;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for accessing BmiResult documents in MongoDB.
 * Extends MongoRepository to provide standard CRUD operations.
 */
@Repository
public interface BmiResultRepository extends MongoRepository<BmiResult, String> {
}