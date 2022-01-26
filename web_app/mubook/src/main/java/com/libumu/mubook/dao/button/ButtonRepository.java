package com.libumu.mubook.dao.button;

import com.libumu.mubook.entities.Button;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ButtonRepository extends JpaRepository<Button, Integer> {
    Button findByDescription(String description);
}
