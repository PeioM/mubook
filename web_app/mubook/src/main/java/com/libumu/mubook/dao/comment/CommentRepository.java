package com.libumu.mubook.dao.comment;

import com.libumu.mubook.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> getAllByItemModelItemModelId(Long itemModel_itemModelId);
    Comment findCommentByContent(String content);
    @Query(value = "SELECT MAX(c.comment_id) FROM comment c ", nativeQuery =  true)
    Long getTopId();
}
