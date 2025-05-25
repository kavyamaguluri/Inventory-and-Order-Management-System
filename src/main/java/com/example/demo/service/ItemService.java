package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.ItemRequest;
import com.example.demo.dto.ItemResponse;

public interface ItemService {
    ItemResponse createItem(ItemRequest itemRequest);

    ItemResponse updateItem(Long id, ItemRequest itemRequest);

    void deleteItem(Long id);

    List<ItemResponse> getAllItems();

    ItemResponse getItemById(Long id);
}
