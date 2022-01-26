package com.libumu.mubook.dao.button;

import com.libumu.mubook.entities.Button;

import java.util.List;

public interface ButtonDao {

    List<Button> getAllButtons();
    Button getButton(int buttonId);
    Button getButton(String description);
    void editButton(Button button);
    void deleteButton(int button);
    void deleteButton(Button button);
    void addButton(Button button);
}
