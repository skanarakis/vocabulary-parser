package edu.teikav.robot.parser.services;

import edu.teikav.robot.parser.domain.InventoryItem;
import edu.teikav.robot.parser.util.Trie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;

public class InMemoryInventoryServiceImpl implements InventoryService {

    private Logger logger = LoggerFactory.getLogger(InMemoryInventoryServiceImpl.class);

    private Trie<InventoryItem> inventoryItems = new Trie<>();

    @Override
    public InventoryItem getItem(String term) { return inventoryItems.search(term); }

    @Override
    public Map<String, InventoryItem> getAllItems() {
        return inventoryItems.allElements();
    }

    @Override
    public Map<String, InventoryItem> getAllItemsStartingWith(String prefix) {
        return inventoryItems.elementsOfKeysStartingWith(prefix);
    }

    @Override
    public boolean isInventoried(String term) {
        return inventoryItems.contains(term);
    }

    @Override
    public void save(InventoryItem item) {
        Objects.requireNonNull(item,"Cannot save inventory item with null input");
        String term = item.getTerm();
        logger.debug("Saving term '{}' with item {}", term, item);
        inventoryItems.insert(term, new InventoryItem(item));
    }

    @Override
    public int inventorySize() {
        return inventoryItems.size();
    }

    @Override
    public void clearInventory() {
        logger.info("Clearing {} terms in inventory", inventoryItems.size());
        inventoryItems.deleteAllKeys();
    }
}
