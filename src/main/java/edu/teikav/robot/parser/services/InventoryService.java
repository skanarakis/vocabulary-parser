package edu.teikav.robot.parser.services;

import edu.teikav.robot.parser.domain.InventoryItem;

import java.util.Map;

public interface InventoryService {

    void save(InventoryItem item);

    InventoryItem getItem(String term);

    Map<String, InventoryItem> getAllItems();

    boolean isInventoried(String term);

    int inventorySize();

    void clearInventory();
}
