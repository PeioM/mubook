package com.libumu.mubook.dao.specification;

import com.libumu.mubook.entities.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SpecificationRepository extends JpaRepository<Specification, Long> {
    @Query(value = "SELECT specification_id FROM specification", nativeQuery = true)
    List<Object[]> getAllSpecificationId();
}
