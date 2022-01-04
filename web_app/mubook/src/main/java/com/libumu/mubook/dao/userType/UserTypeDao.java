package com.libumu.mubook.dao.userType;

import com.libumu.mubook.entities.UserType;

import java.util.List;

public interface UserTypeDao {

    public List<UserType> getAllUserTypes();
    public UserType getUserType(String id);
    public void editUserType(UserType userType);
    public void deleteUserType(String id);
    public void deleteUserType(UserType userType);
    public void addUserType(UserType userType);

}
