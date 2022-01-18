package com.libumu.mubook.dao.incidenceSeverity;

import com.libumu.mubook.entities.IncidenceSeverity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncidenceSeverityRepository extends JpaRepository<IncidenceSeverity, Integer> {
    IncidenceSeverity getIncidenceSeverityByDescription(String description);
}
