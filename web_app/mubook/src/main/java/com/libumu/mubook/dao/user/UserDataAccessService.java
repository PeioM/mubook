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
        return repository.findAll();
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

    @Override
    public User getUserByUsername(String username) {
        return repository.findByUsername(username);
    }

    public User getUserByDNI(String username) {
        return repository.findByDNI(username);
    }

    @Override
    public User getUserByEmail(String username) {
        return repository.findByEmail(username);
    }

    @Override
    public int countUsersByAge(int low, int high) {
        return repository.countUsersByAge(low, high);
    }

    @Override
    public List<Object[]> countUsersByAgeWithoutMT() {
        return repository.countUsersByAgeWithoutMT();
    }

    @Override
    public List<Object[]> countUsersByIncidence(int numIncidence) {
        return repository.countUsersByIncidence(numIncidence);
    }

    @Override
    public List<Object[]> countUsersByIncidenceWithoutMT() {
        return repository.countUsersByIncidenceWithoutMT();
    }

    @Override
    public int countUserByUsernameAndUserIdIsNot(String username, Long userId) {
        return repository.countUserByUsernameAndUserIdIsNot(username, userId);
    }

    @Override
    public int countUserByEmailAndUserIdIsNot(String email, Long userId) {
        return repository.countUserByEmailAndUserIdIsNot(email, userId);
    }

}
