package com.libumu.mubook.dao.incidence;

import com.libumu.mubook.entities.Incidence;
import com.libumu.mubook.entities.User;

import java.sql.Date;
import java.util.List;

public interface IncidenceDao {
    List<Incidence> getAllIncidences();
    Incidence getIncidence(long id);
    void editIncidence(Incidence incidence);
    void deleteIncidence(long id);
    void deleteIncidence(Incidence incidence);
    void addIncidence(Incidence incidence);
    List<Integer> getIncidenceWithSeverityId(int incidenceSeverityId);
    List<Integer> getIncidenceWithUserId(long incidenceUserId);
    List<Incidence> getAllByUser(User user);
    List<Incidence> getIncidencesByEndDateIsAfterAndUser_UserId(Date endDate, Long user_userId);
    int countSumIncidenceByUserId(long userId);
    Long getTopId();
}
