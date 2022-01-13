package com.libumu.mubook.dao.incidence;

import com.libumu.mubook.entities.Incidence;
import org.springframework.data.jpa.repository.JpaRepository;


public interface IncidenceRepository extends JpaRepository<Incidence, Long> {
}
