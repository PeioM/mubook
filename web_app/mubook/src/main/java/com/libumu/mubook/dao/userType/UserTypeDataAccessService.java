package com.libumu.mubook.dao.userType;

import com.libumu.mubook.entities.UserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserTypeDataAccessService implements UserTypeDao {

    @Autowired
    private UserTypeRepository repository;

    @Override
    public List<UserType> getAllUserTypes() {
        return (List<UserType>) repository.findAll();
    }

    @Override
    public UserType getUserType(int id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public void editUserType(UserType userType) {
        repository.save(userType);
    }

    @Override
    public void deleteUserType(int id) {
        repository.deleteById(id);
    }

    @Override
    public void deleteUserType(UserType userType) {
        repository.delete(userType);
    }

    @Override
    public void addUserType(UserType userType) {
        repository.save(userType);
    }
}
