package com.libumu.mubook.dao.specificationList;

import com.libumu.mubook.entities.SpecificationList;

import java.util.List;

public interface SpecificationListDao {
    void addSpecificationList(SpecificationList specificationList);
    List<SpecificationList> getSpecificationListsByItemModel_ItemModelId(Long itemModel_itemModelId);
    SpecificationList findSpecificationListBySpecificationListId(Long specificationListId);
    void deleteSpecificationList(SpecificationList specificationList);
    Long getTopId();
}
