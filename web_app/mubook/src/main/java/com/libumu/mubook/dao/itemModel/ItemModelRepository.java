package com.libumu.mubook.dao.itemModel;

import java.util.List;

import com.libumu.mubook.entities.ItemModel;
import com.libumu.mubook.entities.ItemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

// This will be AUTO IMPLEMENTED by Spring into a Bean called itemRepository
// CRUD refers Create, Read, Update, Delete

public interface ItemModelRepository extends JpaRepository<ItemModel, Long> {
    int countItemModelByIdentifierAndItemModelIdNotLike(String identifier, Long itemModelId);

    @Query(value = "SELECT item_model_id FROM item_model", nativeQuery = true)
    public List<Object[]> getAllItemModelId();
    List<ItemModel> findAllByItemTypeItemTypeId(Integer itemType_itemTypeId);
}
