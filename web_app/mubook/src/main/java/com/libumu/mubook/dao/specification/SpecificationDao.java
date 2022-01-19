package com.libumu.mubook.dao.specification;

import com.libumu.mubook.entities.Specification;

import java.util.List;

public interface SpecificationDao {

    public List<Specification> getAllSpecifications();
    public List<Integer> getAllSpecificationIds();
    public Specification getSpecification(long id);
    public void editSpecification(Specification specification);
    public void deleteSpecification(long id);
    public void deleteSpecification(Specification specification);
    public void addSpecification(Specification specification);
    public List<Integer> getSpecificationWithModelId(long itemModelId);

}
