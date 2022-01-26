package com.libumu.mubook.dao.userActivity;

import com.libumu.mubook.entities.UserActivity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserActivityDataAccessService implements UserActivityDao {

    @Autowired
    private UserActivityRepository repository;

    @Override
    public List<UserActivity> getAllUserActivities() {
        return (List<UserActivity>) repository.findAll();
    }

    @Override
    public UserActivity getUserActivity(int id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public void editUserActivity(UserActivity userActivity) {
        repository.save(userActivity);
    }

    @Override
    public void deleteUserActivity(int id) {
        repository.deleteById(id);
    }

    @Override
    public void deleteUserActivity(UserActivity userActivity) {
        repository.delete(userActivity);
    }

    @Override
    public void addUserActivity(UserActivity userActivity) {
        repository.save(userActivity);
    }
}
