package com.libumu.mubook.dao.specification;

import com.libumu.mubook.entities.Specification;

import java.util.List;

public interface SpecificationDao {

     List<Specification> getAllSpecifications();
     List<Integer> getAllSpecificationIds();
     Specification getSpecification(int id);
     void editSpecification(Specification specification);
     void deleteSpecification(int id);
     void deleteSpecification(Specification specification);
     void addSpecification(Specification specification);
     List<Integer> getSpecificationWithModelId(long itemModelId);
     List<Object[]> getAllSpecificationAndValuesByItemType(int itemTypeId);
}
