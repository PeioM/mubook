package com.libumu.mubook.dao.user;

import com.libumu.mubook.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String userName);

    @Query(value = "SELECT COUNT(user_id), '?1 - ?2' AS RANGO "+
                    "FROM user "+
                    "WHERE FLOOR(DATEDIFF(CURRENT_DATE, borndate)/365) BETWEEN ?1 AND ?2", nativeQuery = true)
    public int countUsersByAge(int low, int high);

    @Query(value = "SELECT COUNT(user_id), '0 - 12' AS RANGO "+
                    "FROM user "+
                    "WHERE FLOOR(DATEDIFF(CURRENT_DATE, borndate)/365) BETWEEN 0 AND 12 "+
                    "UNION "+
                    "SELECT COUNT(user_id), '13 - 18' AS RANGO "+
                    "FROM user "+
                    "WHERE FLOOR(DATEDIFF(CURRENT_DATE, borndate)/365) BETWEEN 13 AND 18 "+
                    "UNION "+
                    "SELECT COUNT(user_id), '19 - 30' AS RANGO "+
                    "FROM user "+
                    "WHERE FLOOR(DATEDIFF(CURRENT_DATE, borndate)/365) BETWEEN 19 AND 30 "+
                    "UNION "+
                    "SELECT COUNT(user_id), '31 - 50' AS RANGO "+
                    "FROM user "+
                    "WHERE FLOOR(DATEDIFF(CURRENT_DATE, borndate)/365) BETWEEN 31 AND 50 "+
                    "UNION "+
                    "SELECT COUNT(user_id), '51+'  AS RANGO "+
                    "FROM user "+
                    "WHERE FLOOR(DATEDIFF(CURRENT_DATE, borndate)/365) > 50 ", nativeQuery = true)
    public List<Object[]> countUsersByAgeWithoutMT();

    @Query(value = "SELECT COUNT(a.user_id), a.peso "+
                    "FROM (SELECT u.user_id, SUM(s.importance) AS peso "+
                            "FROM user u "+
                                "JOIN incidence i on u.user_id = i.user_id "+
                                "JOIN incidence_severity s on s.incidence_severity_id = i.incidence_severity_id "+
                            "WHERE s.importance = ?1 "+
                            "GROUP BY u.user_id) AS a "+
                    "GROUP BY a.peso;", nativeQuery = true)
    public List<Object[]> countUsersByIncidence(int numIncidence);

    @Query(value = "SELECT COUNT(a.user_id), a.peso "+
                    "FROM (SELECT u.user_id, SUM(s.importance) AS peso "+
                            "FROM user u "+
                                "JOIN incidence i on u.user_id = i.user_id "+
                                "JOIN incidence_severity s on s.incidence_severity_id = i.incidence_severity_id "+
                            "GROUP BY u.user_id) AS a "+
                    "GROUP BY a.peso;", nativeQuery = true)
    public List<Object[]> countUsersByIncidenceWithoutMT();
}
