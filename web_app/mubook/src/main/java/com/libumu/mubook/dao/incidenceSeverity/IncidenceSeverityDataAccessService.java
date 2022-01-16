package com.libumu.mubook.dao.incidenceSeverity;

import com.libumu.mubook.entities.IncidenceSeverity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IncidenceSeverityDataAccessService implements IncidenceSeverityDao {

    @Autowired
    private IncidenceSeverityRepository repository;

    @Override
    public List<IncidenceSeverity> getAllIncidenceSeverities() {
        return (List<IncidenceSeverity>) repository.findAll();
    }

    @Override
    public IncidenceSeverity getIncidenceSeverity(int id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public void editIncidenceSeverity(IncidenceSeverity incidence) {
        repository.save(incidence);
    }

    @Override
    public void deleteIncidenceSeverity(int id) {
        repository.deleteById(id);
    }

    @Override
    public void deleteIncidenceSeverity(IncidenceSeverity incidence) {
        repository.delete(incidence);
    }

    @Override
    public void addIncidenceSeverity(IncidenceSeverity incidence) {
        repository.save(incidence);
    }

    @Override
    public IncidenceSeverity getIncidenceSeverityByDescription(String description) {
        return repository.getIncidenceSeverityByDescription(description);
    }

}
