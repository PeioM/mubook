package com.libumu.mubook.dao.comment;

import com.libumu.mubook.entities.Comment;

import java.util.List;

public interface CommentDao {

    List<Comment> getAllComments();
    List<Comment> getAllComentsByItemModelId(long itemModelId);
    Comment getComment(long commentId);
    void editComment(Comment comment);
    void deleteComment(long commentId);
    void deleteComent(Comment comment);
    void addComent(Comment comment);
    Comment findCommentByContent(String content);
    Long getTopId();
}
