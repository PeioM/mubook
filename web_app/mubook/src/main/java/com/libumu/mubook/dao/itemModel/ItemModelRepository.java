package com.libumu.mubook.dao.itemModel;

import com.libumu.mubook.entities.ItemModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

// This will be AUTO IMPLEMENTED by Spring into a Bean called itemRepository
// CRUD refers Create, Read, Update, Delete

public interface ItemModelRepository extends JpaRepository<ItemModel, Long> {
    int countItemModelByIdentifierAndItemModelIdNotLike(String identifier, Long itemModelId);

    @Query(value = "SELECT item_model_id FROM item_model", nativeQuery = true)
    public List<Object[]> getAllItemModelId();
    List<ItemModel> findAllByItemTypeItemTypeId(Integer itemType_itemTypeId);
    List<ItemModel> findAllByItemModelIdInAndSpecificationListsSpecificationSpecificationIdAndSpecificationListsValue(Collection<Long> itemModelId, Integer specificationLists_specification_specificationId, String specificationLists_value);
}
