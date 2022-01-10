package com.libumu.mubook.dao.user;

import com.libumu.mubook.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String userName);

    @Query(value = "SELECT COUNT(userId)"+
                    "FROM USER"+
                    "WHERE FLOOR(DATEDIFF(CURRENT_DATE, bornDate)/365) BETWEEN ?1 AND ?2", nativeQuery = true)
    public int countUsersByAge(int low, int high);

    @Query(value = "SELECT COUNT(a.userId), a.peso"+
                    "FROM (SELECT u.userId, SUM(s.peso) AS peso"+
                            "FROM user u"+
                                "JOIN incidence i on u.userId = i.userId"+
                                "JOIN incidence_severity s on s.incidenceSeverityId = i.incidenceSeverityId"+
                            "WHERE peso = ?1"+
                            "GROUP BY u.userId) AS a"+
                    "GROUP BY a.peso;", nativeQuery = true)
    public List<Object[]> countUsersByIncidence(int numIncidence);
}
