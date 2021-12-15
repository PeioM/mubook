package com.libumu.mubook.dao.user;

import com.libumu.mubook.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDataAccessService implements UserDao {

    @Autowired
    private UserRepository repository;

    @Override
    public List<User> getAllUsers() {
        return (List<User>) repository.findAll();
    }

    @Override
    public User getUser(long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public void editUser(User user) {
        repository.save(user);
    }

    @Override
    public void deleteUser(long id) {
        repository.deleteById(id);
    }

    @Override
    public void deleteUser(User user) {
        repository.delete(user);
    }

    @Override
    public void addUser(User user) {
        repository.save(user);
    }
}
