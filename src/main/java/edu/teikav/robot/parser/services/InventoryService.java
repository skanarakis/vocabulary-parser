package edu.teikav.robot.parser.services;

import edu.teikav.robot.parser.domain.InventoryItem;

import java.util.Map;

public interface InventoryService {

    InventoryItem getItem(String term);

    Map<String, InventoryItem> getAllItems();

    boolean isInventoried(String term);

    void save(InventoryItem item);

    int inventorySize();
}
