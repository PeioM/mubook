package com.libumu.mubook.dao.itemModel;

import java.util.List;

import com.libumu.mubook.entities.ItemModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

// This will be AUTO IMPLEMENTED by Spring into a Bean called itemRepository
// CRUD refers Create, Read, Update, Delete

public interface ItemModelRepository extends JpaRepository<ItemModel, Long> {
    @Query(value = "SELECT item_model_id FROM item_model", nativeQuery = true)
    public List<Object[]> getAllItemModelId();
}
