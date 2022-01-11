package com.libumu.mubook.dao.itemType;

import java.util.List;

import com.libumu.mubook.entities.ItemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

// This will be AUTO IMPLEMENTED by Spring into a Bean called itemRepository
// CRUD refers Create, Read, Update, Delete

public interface ItemTypeRepository extends JpaRepository<ItemType, Integer> {
    @Query(value = "SELECT item_type_id "+
                    "FROM item_type", nativeQuery = true)
    public List<Object[]> getAllItemTypeId();
}
