package com.libumu.mubook.dao.specification;

import com.libumu.mubook.entities.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SpecificationDataAccessService implements SpecificationDao {

    @Autowired
    private SpecificationRepository repository;

    @Override
    public List<Specification> getAllSpecifications() {
        return (List<Specification>) repository.findAll();
    }

    @Override
    public List<Integer> getAllSpecificationIds() {
        List<Object[]> result = repository.getAllSpecificationId();
        List<Integer> ids = new ArrayList<>();
        for(Object[] i : result){
            ids.add((Integer) i[0]);
        }
        return ids;
    }

    @Override
    public Specification getSpecification(int id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public void editSpecification(Specification specification) {
        repository.save(specification);
    }

    @Override
    public void deleteSpecification(int id) {
        repository.deleteById(id);
    }

    @Override
    public void deleteSpecification(Specification specification) {
        repository.delete(specification);
    }

    @Override
    public void addSpecification(Specification specification) {
        repository.save(specification);
    }
    
    @Override
    public List<Integer> getSpecificationWithModelId(long itemModelId) {
        return null;
    }

    @Override
    public List<Object[]> getAllSpecificationAndValuesByItemType(int itemTypeId) {
        return repository.getAllSpecificationAndValuesByItemType(itemTypeId);
    }
}
