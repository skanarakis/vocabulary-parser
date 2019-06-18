package edu.teikav.robot.parser.services;

import edu.teikav.robot.parser.domain.InventoryItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class InMemoryInventoryServiceImpl implements InventoryService {

    private Logger logger = LoggerFactory.getLogger(InMemoryInventoryServiceImpl.class);

    private Map<String, InventoryItem> inventoryItemsMap = new HashMap<>();

    @Override
    public InventoryItem getItem(String term) {
        return inventoryItemsMap.get(term);
    }

    @Override
    public Map<String, InventoryItem> getAllItems() {
        return Collections.unmodifiableMap(inventoryItemsMap);
    }

    @Override
    public boolean isInventoried(String term) {
        return inventoryItemsMap.containsKey(term);
    }

    @Override
    public void save(InventoryItem item) {
        Objects.requireNonNull(item,"Cannot save inventory item with null input");
        String term = item.getTerm();
        InventoryItem existingItem = inventoryItemsMap.putIfAbsent(term, new InventoryItem(item));
        if (existingItem != null) {
            // TODO: Currently we only overwrite. We need to update with a smarter way
            existingItem.setTermType(item.getTermType());
            existingItem.setTranslation(item.getTranslation());
            existingItem.setExample(item.getExample());
            existingItem.setPronunciation(item.getPronunciation());
            existingItem.setVerbParticiples(item.getVerbParticiples());
            existingItem.setDerivative(item.getDerivative());
            existingItem.setOpposite(item.getOpposite());
            logger.debug("INVENTORY: Updating current item [{}]. Updated status\n{}", term, inventoryItemsMap.get(term));
        } else {
            logger.debug("INVENTORY: Storing new item [{}] with status\n{}", term, item);
        }
    }

    @Override
    public int inventorySize() {
        return inventoryItemsMap.size();
    }
}
