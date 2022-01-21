package com.libumu.mubook.dao.button;

import com.libumu.mubook.entities.Button;
import com.libumu.mubook.entities.ButtonClick;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ButtonDataAccessService implements ButtonDao {

    @Autowired
    private ButtonRepository repository;


    @Override
    public List<Button> getAllButtons() {
        return repository.findAll();
    }

    @Override
    public Button getButton(int buttonId) {
        return repository.findById(buttonId).orElse(null);
    }

    @Override
    public Button getButton(String description) {
        return repository.findByDescription(description);
    }

    @Override
    public void editButton(Button button) {
        repository.save(button);
    }

    @Override
    public void deleteButton(int button) {
        repository.deleteById(button);
    }

    @Override
    public void deleteButton(Button button) {
        repository.delete(button);
    }

    @Override
    public void addButton(Button button) {
        repository.save(button);
    }
}
