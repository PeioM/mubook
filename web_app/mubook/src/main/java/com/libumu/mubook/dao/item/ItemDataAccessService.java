package com.libumu.mubook.dao.item;

import com.libumu.mubook.entities.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemDataAccessService implements ItemDao {

    @Autowired
    private ItemRepository repository;

    @Override
    public List<Item> getAllItems() {
        return (List<Item>) repository.findAll();
    }

    @Override
    public Item getItem(long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public void editItem(Item item) {
        repository.save(item);
    }

    @Override
    public void deleteItem(long id) {
        repository.deleteById(id);
    }

    @Override
    public void deleteItem(Item item) {
        repository.delete(item);
    }

    @Override
    public void addItem(Item item) {
        repository.save(item);
    }

    @Override
    public List<Object[]> getItemWithModelId(long itemModelId) {
        return repository.getItemWithModelId(itemModelId);
    }

    @Override
    public List<Item> getItemByItemModelItemModelId(Long itemModel_itemModelId) {
        return repository.getItemByItemModelItemModelId(itemModel_itemModelId);
    }

    @Override
    public Long getTopId() {
        return repository.getTopId();
    }
}
