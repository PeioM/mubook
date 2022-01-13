package com.libumu.mubook.dao.specification;

import com.libumu.mubook.entities.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecificationDataAccessService implements SpecificationDao {

    @Autowired
    private SpecificationRepository repository;

    @Override
    public List<Specification> getAllSpecifications() {
        return (List<Specification>) repository.findAll();
    }

    @Override
    public Specification getSpecification(long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public void editSpecification(Specification specification) {
        repository.save(specification);
    }

    @Override
    public void deleteSpecification(long id) {
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
}
