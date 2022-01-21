package com.libumu.mubook.dao.status;

import com.libumu.mubook.entities.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatusDataAccessService implements StatusDao {

    @Autowired
    private StatusRepository repository;

    @Override
    public List<Status> getAllStatus() {
        return (List<Status>) repository.findAll();
    }

    @Override
    public Status getStatus(long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public void editStatus(Status status) {
        repository.save(status);
    }

    @Override
    public void deleteStatus(long id) {
        repository.deleteById(id);
    }

    @Override
    public void deleteStatus(Status status) {
        repository.delete(status);
    }

    @Override
    public void addStatus(Status status) {
        repository.save(status);
    }
    
    @Override
    public List<Integer> getStatusWithItemId(long itemId) {
        return null;
    }

    @Override
    public Status getStatusByDescription(String description) {
        return repository.getStatusByDescription(description);
    }
}
