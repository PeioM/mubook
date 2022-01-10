package com.libumu.mubook.dao.itemType;

import com.libumu.mubook.entities.ItemType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemTypeDataAccessService implements ItemTypeDao {

    @Autowired
    private ItemTypeRepository repository;

    @Override
    public List<ItemType> getAllItemTypes() {
        return (List<ItemType>) repository.findAll();
    }

    @Override
    public ItemType getItemType(String id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public void editItemType(ItemType itemType) {
        repository.save(itemType);
    }

    @Override
    public void deleteItemType(String id) {
        repository.deleteById(id);
    }

    @Override
    public void deleteItemType(ItemType itemType) {
        repository.delete(itemType);
    }

    @Override
    public void addItemType(ItemType itemType) {
        repository.save(itemType);
    }

    @Override
    public List<Integer> getAllItemTypeId() {
        return repository.getAllItemTypeId();
    }

}
