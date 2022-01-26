package com.libumu.mubook.dao.buttonClick;

import com.libumu.mubook.entities.ButtonClick;

import java.util.List;

public interface ButtonClickDao {

    List<ButtonClick> getAllButtonClick();
    List<ButtonClick> getAllButtonClicksByUser(long userId);
    ButtonClick getButtonClick(long buttonClickId);
    void editButtonClick(ButtonClick buttonClick);
    void deleteButtonClick(long buttonClick);
    void deleteButtonClick(ButtonClick buttonClick);
    void addButtonClick(ButtonClick buttonClick);
    long countButtonClicks();
}
