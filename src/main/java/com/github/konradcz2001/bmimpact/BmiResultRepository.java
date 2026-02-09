package com.github.konradcz2001.bmimpact;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for accessing BmiResult data using JPA.
 */
public interface BmiResultRepository extends JpaRepository<BmiResult, Long> {
}