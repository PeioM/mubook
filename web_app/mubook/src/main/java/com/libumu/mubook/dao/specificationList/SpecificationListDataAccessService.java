package com.libumu.mubook.dao.specificationList;

import com.libumu.mubook.entities.SpecificationList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecificationListDataAccessService implements SpecificationListDao{

    @Autowired
    private SpecificationListRepository repository;

    @Override
    public void addSpecificationList(SpecificationList specificationList) {
        repository.save(specificationList);
    }

    @Override
    public List<SpecificationList> getSpecificationListsByItemModel_ItemModelId(Long itemModel_itemModelId) {
        return repository.getSpecificationListsByItemModel_ItemModelId(itemModel_itemModelId);
    }

    @Override
    public SpecificationList findSpecificationListBySpecificationListId(Long specificationListId) {
        return repository.findSpecificationListBySpecificationListId(specificationListId);
    }

    @Override
    public void deleteSpecificationList(SpecificationList specificationList) {
        repository.deleteById(specificationList.getSpecificationListId());
    }

    @Override
    public Long getTopId() {
        return repository.getTopId();
    }
}
