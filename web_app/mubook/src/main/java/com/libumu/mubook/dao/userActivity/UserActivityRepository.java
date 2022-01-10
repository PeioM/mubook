package com.libumu.mubook.dao.userActivity;

import com.libumu.mubook.entities.UserActivity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserActivityRepository extends JpaRepository<UserActivity, Integer> {
}
