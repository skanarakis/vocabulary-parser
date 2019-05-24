package edu.teikav.robot.parser.dtos;

import edu.teikav.robot.parser.domain.InventoryItem;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class InventoryItemsDTO {

    private Map<String, InventoryItemDTO> vocabularyInventory;

    public InventoryItemsDTO() {}

    public InventoryItemsDTO(Map<String, InventoryItem> items) {

        Objects.requireNonNull(items, "Items argument passed for InventoryItemsDTO constructor is null");
        vocabularyInventory = new HashMap<>();
        items.entrySet().stream()
                .forEach(entry ->  vocabularyInventory.put(entry.getKey(), new InventoryItemDTO(entry.getValue())));
    }

    public Map<String, InventoryItemDTO> getVocabularyInventory() {
        return vocabularyInventory;
    }

    public void setVocabularyInventory(Map<String, InventoryItemDTO> vocabularyInventory) {
        this.vocabularyInventory = vocabularyInventory;
    }
}
