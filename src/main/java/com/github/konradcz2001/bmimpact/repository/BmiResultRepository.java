package com.github.konradcz2001.bmimpact.repository;

import com.github.konradcz2001.bmimpact.model.BmiResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for accessing BmiResult documents in MongoDB.
 */
@Repository
public interface BmiResultRepository extends MongoRepository<BmiResult, String> {
    Page<BmiResult> findByUsername(String username, Pageable pageable);

    void deleteByUsername(String username);

    void deleteByIdAndUsername(String id, String username);
}