package com.libumu.mubook.dao.item;

import java.util.List;

import com.libumu.mubook.entities.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query(value = "SELECT item_id "+
                    "FROM item i"+
                    "   JOIN item_model im on im.item_model_id = i.item_model_id "+
                    "WHERE im.item_model_id = ?1", nativeQuery = true)
    public List<Object[]> getItemWithModelId(long itemModelId);
}
