package com.libumu.mubook.dao.incidenceSeverity;

import com.libumu.mubook.entities.IncidenceSeverity;

import java.util.List;

public interface IncidenceSeverityDao {

    public List<IncidenceSeverity> getAllIncidenceSeverities();
    public IncidenceSeverity getIncidenceSeverity(int id);
    public void editIncidenceSeverity(IncidenceSeverity incidence);
    public void deleteIncidenceSeverity(int id);
    public void deleteIncidenceSeverity(IncidenceSeverity incidence);
    public void addIncidenceSeverity(IncidenceSeverity incidence);
    IncidenceSeverity getIncidenceSeverityByDescription(String description);

}
