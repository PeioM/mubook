package com.libumu.mubook.dao.incidence;

import com.libumu.mubook.entities.Incidence;
import com.libumu.mubook.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;


public interface IncidenceRepository extends JpaRepository<Incidence, Long> {
    List<Incidence> getAllByUser(User user);
}
