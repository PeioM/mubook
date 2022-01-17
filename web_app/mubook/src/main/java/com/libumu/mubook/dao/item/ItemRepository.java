package com.libumu.mubook.dao.item;

import java.util.List;

import com.libumu.mubook.entities.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> getItemByItemModelItemModelId(Long itemModel_itemModelId);

    @Query(value = "SELECT item_id "+
                    "FROM item i"+
                    "   JOIN item_model im on im.item_model_id = i.item_model_id "+
                    "WHERE im.item_model_id = ?1", nativeQuery = true)
    public List<Object[]> getItemWithModelId(long itemModelId);
}
