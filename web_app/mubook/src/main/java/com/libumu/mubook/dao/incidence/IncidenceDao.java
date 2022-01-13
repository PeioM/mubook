package com.libumu.mubook.dao.incidence;

import com.libumu.mubook.entities.Incidence;

import java.util.List;

public interface IncidenceDao {
    public List<Incidence> getAllIncidences();
    public Incidence getIncidence(long id);
    public void editIncidence(Incidence incidence);
    public void deleteIncidence(long id);
    public void deleteIncidence(Incidence incidence);
    public void addIncidence(Incidence incidence);
    public List<Integer> getIncidenceWithSeverityId(int incidenceSeverityId);
    public List<Integer> getIncidenceWithUserId(long incidenceUserId);
}
