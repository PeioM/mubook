package com.libumu.mubook.dao.buttonClick;

import com.libumu.mubook.entities.ButtonClick;
import com.libumu.mubook.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ButtonClickRepository extends JpaRepository<ButtonClick, Long> {
    List<ButtonClick> findAllByUserUserId(Long user_userId);
}
