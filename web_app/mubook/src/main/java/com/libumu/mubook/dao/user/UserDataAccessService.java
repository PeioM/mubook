package com.libumu.mubook.dao.user;

import com.libumu.mubook.api.AjaxController;
import com.libumu.mubook.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
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
    public List<User> getUsersBetweenContaining(int page, String containStr) {
        int start = (page-1)* AjaxController.ITEMS_PER_PAGE;
        int quantity = AjaxController.ITEMS_PER_PAGE;
        return repository.getUsersBetweenContaining(start, quantity, "%"+containStr+"%");
    }

    @Override
    public List<User> getUsersByTypeAndBetweenAndContainig(String userType, int page, String containingStr) {
        int start = (page-1)* AjaxController.ITEMS_PER_PAGE;
        int quantity = AjaxController.ITEMS_PER_PAGE;
        return repository.getUsersByTypeAndBetweenAndContaining(userType, start, quantity, "%"+containingStr+"%");
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
    public User findUserByUserId(Long userId) {
        return repository.findUserByUserId(userId);
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

    @Override
    public int countUsersByUsername(String username) {
        return repository.countUserByUsername(username);
    }

    @Override
    public int countUsersByEmail(String email) {
        return repository.countUserByEmail(email);
    }

    @Override
    public int getuserCountContaining(String containStr) {
        List<Object[]> result = repository.getUserCountContaining("%"+containStr+"%");
        BigInteger totalUsers = (BigInteger) result.get(0)[0];
        return totalUsers.intValue();
    }

    @Override
    public int getuserCountByTypeAndContaining(String userType, String containStr) {
        List<Object[]> result = repository.getUserCountByTypeAndContaining(userType, "%"+containStr+"%");
        BigInteger totalUsers = (BigInteger) result.get(0)[0];
        return totalUsers.intValue();
    }

}
