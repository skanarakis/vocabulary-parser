package edu.teikav.robot.parser.services;

import edu.teikav.robot.parser.domain.InventoryItem;

public interface InventoryService {

    InventoryItem getInventoryItem(String term);

    boolean existsInventoryItem(String term);

    void saveNewInventoryItem(InventoryItem item);

    int numberOfInventoryTerms();

    void empty();
}
