package com.libumu.mubook.dao.comment;

import com.libumu.mubook.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> getAllByItemModelItemModelId(Long itemModel_itemModelId);
}
