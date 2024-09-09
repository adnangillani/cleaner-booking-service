package com.cleanersbooking.service.repository;

import com.cleanersbooking.service.entity.Cleaner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CleanerRepository extends JpaRepository<Cleaner, Long> {

}
