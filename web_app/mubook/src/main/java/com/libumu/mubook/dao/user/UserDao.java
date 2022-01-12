package com.libumu.mubook.dao.user;

import com.libumu.mubook.entities.User;

import java.util.List;

public interface UserDao {

    public List<User> getAllUsers();
    public User getUser(long id);
    public void editUser(User user);
    public void deleteUser(long id);
    public void deleteUser(User user);
    public void addUser(User user);
    public User getUserByUsername(String username);
    public User getUserByDNI(String username);
    public User getUserByEmail(String username);
    
    public int countUsersByAge(int low, int high);
    public List<Object[]> countUsersByAgeWithoutMT();
    public List<Object[]> countUsersByIncidence(int numIncidence);
    public List<Object[]> countUsersByIncidenceWithoutMT();

}
