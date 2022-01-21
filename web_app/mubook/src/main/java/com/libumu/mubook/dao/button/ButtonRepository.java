package com.libumu.mubook.dao.button;

import com.libumu.mubook.entities.Button;
import com.libumu.mubook.entities.ButtonClick;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ButtonRepository extends JpaRepository<Button, Integer> {
    Button findByDescription(String description);
}
