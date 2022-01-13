package com.libumu.mubook.dao.incidence;

import com.libumu.mubook.entities.Incidence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IncidenceDataAccessService implements IncidenceDao {

    @Autowired
    private IncidenceRepository repository;

    @Override
    public List<Incidence> getAllIncidences() {
        return (List<Incidence>) repository.findAll();
    }

    @Override
    public Incidence getIncidence(long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public void editIncidence(Incidence incidence) {
        repository.save(incidence);
    }

    @Override
    public void deleteIncidence(long id) {
        repository.deleteById(id);
    }

    @Override
    public void deleteIncidence(Incidence incidence) {
        repository.delete(incidence);
    }

    @Override
    public void addIncidence(Incidence incidence) {
        repository.save(incidence);
    }

    @Override
    public List<Integer> getIncidenceWithSeverityId(int incidenceSeverityId) {
        return null;
    }
    @Override
    public List<Integer> getIncidenceWithUserId(long incidenceUserId) {
        return null;
    }
}
