package edu.teikav.robot.parser.services;

import edu.teikav.robot.parser.domain.InventoryItem;

public interface InventoryService {

    InventoryItem getItem(String term);

    boolean isInventoried(String term);

    void save(InventoryItem item);

    int inventorySize();
}
