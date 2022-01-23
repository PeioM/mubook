package com.libumu.mubook.dao.user;

import com.libumu.mubook.entities.User;

import java.util.List;

 public interface UserDao {
     List<User> getAllUsers();
     List<User> getUsersBetweenContaining(int page, String containStr);
     List<User> getUsersByTypeAndBetweenAndContainig(String userType, int page, String containingStr);

     User getUser(long id);
     void editUser(User user);
     void deleteUser(long id);
     void deleteUser(User user);
     void addUser(User user);
     User getUserByUsername(String username);
     User getUserByDNI(String username);
     User getUserByEmail(String username);
     User findUserByUserId(Long userId);
    
     int countUsersByAge(int low, int high);
     List<Object[]> countUsersByAgeWithoutMT();
     List<Object[]> countUsersByIncidence(int numIncidence);
     List<Object[]> countUsersByIncidenceWithoutMT();
     int countUserByUsernameAndUserIdIsNot(String username, Long userId);
     int countUserByEmailAndUserIdIsNot(String email, Long userId);
     int countUsersByUsername(String username);
     int countUsersByEmail(String email);


     int getuserCountContaining(String containStr);
     int getuserCountByTypeAndContaining(String userType, String containStr);
 }
