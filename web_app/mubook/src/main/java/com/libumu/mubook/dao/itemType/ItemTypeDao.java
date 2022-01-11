package com.libumu.mubook.dao.itemType;

import com.libumu.mubook.entities.ItemType;

import java.util.List;

public interface ItemTypeDao {

    public List<ItemType> getAllItemTypes();
    public ItemType getItemType(int id);
    public void editItemType(ItemType itemType);
    public void deleteItemType(int id);
    public void deleteItemType(ItemType itemType);
    public void addItemType(ItemType itemType);
    public List<Object[]> getAllItemTypeId();

}
