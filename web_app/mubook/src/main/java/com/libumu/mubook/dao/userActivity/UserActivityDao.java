package com.libumu.mubook.dao.userActivity;

import com.libumu.mubook.entities.UserActivity;
import com.libumu.mubook.entities.UserType;

import java.util.List;

public interface UserActivityDao {

    public List<UserActivity> getAllUserActivities();
    public UserActivity getUserActivity(int id);
    public void editUserActivity(UserActivity userType);
    public void deleteUserActivity(int id);
    public void deleteUserActivity(UserActivity userType);
    public void addUserActivity(UserActivity userType);

}
