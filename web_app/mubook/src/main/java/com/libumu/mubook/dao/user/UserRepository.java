package com.libumu.mubook.dao.user;

import com.libumu.mubook.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByUserId(Long userId);
    User findByUsername(String userName);
    User findByEmail(String email);
    User findByDNI(String DNI);
    int countUserByUsername(String username);
    int countUserByEmailAndUserIdIsNot(String email, Long userId);
    int countUserByUsernameAndUserIdIsNot(String username, Long userId);
    int countUserByDNIAndUserIdIsNot(String DNI, Long userId);
    int countUserByEmail(String email);
    int countUserByDNI(String DNI);

    @Query(value = "SELECT MAX(u.user_id) FROM user u ", nativeQuery =  true)
    Long getTopId();

    @Query(value =  "SELECT DISTINCT u.* " +
                    "FROM user u " +
                    "WHERE name LIKE ?3 OR surname LIKE ?3 " +
                    "OR email LIKE ?3 OR username LIKE ?3 " +
                    "LIMIT ?1,?2" , nativeQuery = true)
    List<User> getUsersBetweenContaining(int startRow, int quantity, String containStr);

    @Query(value = "SELECT COUNT(user_id), '?1 - ?2' AS RANGO "+
                    "FROM user "+
                    "WHERE FLOOR(DATEDIFF(CURRENT_DATE, borndate)/365) BETWEEN ?1 AND ?2", nativeQuery = true)
    int countUsersByAge(int low, int high);

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
    List<Object[]> countUsersByAgeWithoutMT();

    @Query(value = "SELECT COUNT(a.user_id), a.peso "+
                    "FROM (SELECT u.user_id, SUM(s.importance) AS peso "+
                            "FROM user u "+
                                "JOIN incidence i on u.user_id = i.user_id "+
                                "JOIN incidence_severity s on s.incidence_severity_id = i.incidence_severity_id "+
                            "WHERE s.importance = ?1 "+
                            "GROUP BY u.user_id) AS a "+
                    "GROUP BY a.peso;", nativeQuery = true)
    List<Object[]> countUsersByIncidence(int numIncidence);

    @Query(value = "SELECT COUNT(a.user_id), a.peso "+
                    "FROM (SELECT u.user_id, SUM(s.importance) AS peso "+
                            "FROM user u "+
                                "JOIN incidence i on u.user_id = i.user_id "+
                                "JOIN incidence_severity s on s.incidence_severity_id = i.incidence_severity_id "+
                            "GROUP BY u.user_id) AS a "+
                    "GROUP BY a.peso;", nativeQuery = true)
    List<Object[]> countUsersByIncidenceWithoutMT();

    @Query(value = "SELECT DISTINCT u.* " +
                    "FROM user u " +
                    "JOIN user_type ut on ut.user_type_id = u.user_type_id AND ut.user_type_id LIKE ?1 " +
                    "WHERE name LIKE ?4 OR surname LIKE ?4 " +
                    "OR email LIKE ?4 OR username LIKE ?4 " +
                    "LIMIT ?2,?3" , nativeQuery = true)
    List<User> getUsersByTypeAndBetweenAndContaining(String userType, int start, int quantity, String containingStr);

    @Query(value = "SELECT COUNT(u.user_id) " +
                    "FROM user u " +
                    "WHERE name LIKE ?1 OR surname LIKE ?1 " +
                    "OR email LIKE ?1 OR username LIKE ?1 "
                    , nativeQuery = true)
    List<Object[]> getUserCountContaining(String containStr);

    @Query(value = "SELECT COUNT(u.user_id)" +
                    "FROM user u " +
                    "JOIN user_type ut on ut.user_type_id = u.user_type_id AND ut.user_type_id LIKE ?1 " +
                    "WHERE name LIKE ?2 OR surname LIKE ?2 " +
                    "OR email LIKE ?2 OR username LIKE ?2 "
                    , nativeQuery = true)
    List<Object[]> getUserCountByTypeAndContaining(String userType, String containStr);
}
