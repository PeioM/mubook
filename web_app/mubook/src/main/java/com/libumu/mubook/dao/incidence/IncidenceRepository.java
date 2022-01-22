package com.libumu.mubook.dao.incidence;

import com.libumu.mubook.entities.Incidence;
import com.libumu.mubook.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Date;
import java.util.List;


public interface IncidenceRepository extends JpaRepository<Incidence, Long> {
    List<Incidence> getAllByUser(User user);
    List<Incidence> getIncidencesByEndDateIsAfterAndUser_UserId(Date endDate, Long user_userId);

    @Query(value = "SELECT IFNULL(SUM(s.importance), 0) " +
                    "FROM incidence i " +
                    "   JOIN incidence_severity s on s.incidence_severity_id = i.incidence_severity_id " +
                    "   JOIN user u on u.user_id = i.user_id " +
                    "WHERE u.user_id = ?1 " +
                    "AND i.end_date > CURDATE() ", nativeQuery = true)
    int countSumIncidenceByUserId(long userId);
}
