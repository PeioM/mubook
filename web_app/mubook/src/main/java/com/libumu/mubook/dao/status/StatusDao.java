package com.libumu.mubook.dao.status;

import com.libumu.mubook.entities.Status;

import java.util.List;

public interface StatusDao {

    public List<Status> getAllStatus();
    public Status getStatus(long id);
    public void editStatus(Status specification);
    public void deleteStatus(long id);
    public void deleteStatus(Status specification);
    public void addStatus(Status specification);
    public List<Integer> getStatusWithItemId(long itemId);
    Status getStatusByDescription(String description);

}
