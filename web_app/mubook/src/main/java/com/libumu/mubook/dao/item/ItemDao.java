package com.libumu.mubook.dao.item;

import com.libumu.mubook.entities.Item;

import java.util.List;

public interface ItemDao {

    public List<Item> getAllItems();
    public Item getItem(long id);
    public void editItem(Item item);
    public void deleteItem(long id);
    public void deleteItem(Item item);
    public void addItem(Item item);
    public List<Object[]> getItemWithModelId(long itemModelId);
    List<Item> getItemByItemModelItemModelId(Long itemModel_itemModelId);

}
