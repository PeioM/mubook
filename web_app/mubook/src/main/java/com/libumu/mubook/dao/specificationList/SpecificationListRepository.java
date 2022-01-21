package com.libumu.mubook.dao.specificationList;

import com.libumu.mubook.entities.SpecificationList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpecificationListRepository extends JpaRepository<SpecificationList, Long> {
    List<SpecificationList> getSpecificationListsByItemModel_ItemModelId(Long itemModel_itemModelId);
    SpecificationList findSpecificationListBySpecificationListId(Long specificationListId);
}
