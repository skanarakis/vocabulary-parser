package edu.teikav.robot.parser.services;

import edu.teikav.robot.parser.domain.InventoryItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class InMemoryInventoryServiceImpl implements InventoryService {

    private Logger logger = LoggerFactory.getLogger(InMemoryInventoryServiceImpl.class);

    private Map<String, InventoryItem> inventoryItemsMap = new HashMap<>();

    @Override
    public InventoryItem getItem(String term) {
        return inventoryItemsMap.get(term);
    }

    @Override
    public boolean isInventoried(String term) {
        return inventoryItemsMap.containsKey(term);
    }

    @Override
    public void save(InventoryItem item) {
        InventoryItem previousItem = inventoryItemsMap.putIfAbsent(item.getTerm(), item);
        if (previousItem == null) {
            logger.debug("Saving new Vocabulary Term\n\t{}", item);
        } else {
            logger.debug("Updating Vocabulary Term\n\t{}\nwith\n\t{}", previousItem, item);
            // TODO: Update existing item
        }
    }

    @Override
    public int inventorySize() {
        return inventoryItemsMap.size();
    }
}
