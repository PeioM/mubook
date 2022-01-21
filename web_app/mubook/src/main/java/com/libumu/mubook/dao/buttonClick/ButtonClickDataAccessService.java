package com.libumu.mubook.dao.buttonClick;

import com.libumu.mubook.entities.ButtonClick;
import com.libumu.mubook.entities.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ButtonClickDataAccessService implements ButtonClickDao {

    @Autowired
    private ButtonClickRepository repository;

    @Override
    public List<ButtonClick> getAllButtonClick() {
        return repository.findAll();
    }

    @Override
    public List<ButtonClick> getAllButtonClicksByUser(long userId) {
        return repository.findAllByUserUserId(userId);
    }

    @Override
    public ButtonClick getButtonClick(long buttonClickId) {
        return repository.findById(buttonClickId).orElse(null);
    }

    @Override
    public void editButtonClick(ButtonClick buttonClick) {
        repository.save(buttonClick);
    }

    @Override
    public void deleteButtonClick(long buttonClick) {
        repository.deleteById(buttonClick);
    }

    @Override
    public void deleteButtonClick(ButtonClick buttonClick) {
        repository.delete(buttonClick);
    }

    @Override
    public void addButtonClick(ButtonClick buttonClick) {
        repository.save(buttonClick);
    }
}
