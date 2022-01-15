package com.libumu.mubook.dao.faq;

import java.util.List;

import com.libumu.mubook.entities.Faq;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FaqRepository extends JpaRepository<Faq, Integer> {
    List<Faq> findAll();
}
