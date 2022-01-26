package com.libumu.mubook.dao.comment;

import com.libumu.mubook.entities.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentDataAccessService implements CommentDao{

    @Autowired
    private CommentRepository repository;

    @Override
    public List<Comment> getAllComments() {
        return repository.findAll();
    }

    @Override
    public List<Comment> getAllComentsByItemModelId(long itemModelId) {
        return repository.getAllByItemModelItemModelId(itemModelId);
    }

    @Override
    public Comment getComment(long commentId) {
        return repository.findById(commentId).orElse(null);
    }

    @Override
    public void editComment(Comment comment) {
        repository.save(comment);
    }

    @Override
    public void deleteComment(long commentId) {
        repository.deleteById(commentId);
    }

    @Override
    public void deleteComent(Comment comment) {
        repository.delete(comment);
    }

    @Override
    public void addComent(Comment comment) {
        repository.save(comment);
    }

    @Override
    public Comment findCommentByContent(String content) {
        return repository.findCommentByContent(content);
    }

    @Override
    public Long getTopId() {
        return repository.getTopId();
    }
}
