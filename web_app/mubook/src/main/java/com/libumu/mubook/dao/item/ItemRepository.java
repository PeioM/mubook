package com.libumu.mubook.dao.item;

import com.libumu.mubook.entities.Item;
import com.libumu.mubook.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface ItemRepository extends JpaRepository<Item, Long> {
}
