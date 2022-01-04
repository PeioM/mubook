package com.libumu.mubook.dao.userActivity;

import com.libumu.mubook.entities.UserActivity;
import com.libumu.mubook.entities.UserType;
import org.springframework.data.jpa.repository.JpaRepository;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface UserActivityRepository extends JpaRepository<UserActivity, Integer> {
}
