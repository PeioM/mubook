package com.libumu.mubook.dao.status;

import com.libumu.mubook.entities.Status;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusRepository extends JpaRepository<Status, Long> {
    Status getStatusByDescription(String description);
}
