package com.libumu.mubook.dao.itemType;

import java.util.List;

import com.libumu.mubook.entities.ItemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

// This will be AUTO IMPLEMENTED by Spring into a Bean called itemRepository
// CRUD refers Create, Read, Update, Delete

public interface ItemTypeRepository extends JpaRepository<ItemType, String> {
    @Query("SELECT itemTypeId FROM item_type")
    public List<Integer> getAllItemTypeId();
}
