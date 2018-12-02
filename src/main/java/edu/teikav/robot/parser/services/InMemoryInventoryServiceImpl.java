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

    private Map<String, InventoryItem> inventoryItemMap = new HashMap<>();

    public InventoryItem getInventoryItem(String term) {
        return inventoryItemMap.get(term);
    }

    public boolean existsInventoryItem(String term) {
        return inventoryItemMap.get(term) != null;
    }

    public void saveNewInventoryItem(InventoryItem item) {
        InventoryItem previousItem = inventoryItemMap.putIfAbsent(item.getTerm(), item);
        if (previousItem == null) {
            logger.debug("Saving new Vocabulary Term\n\t{}", item);
        } else {
            logger.debug("Updating Vocabulary Term\n\t{}\nwith\n\t", previousItem, item);
        }
    }

    @Override
    public int numberOfInventoryTerms() {
        return inventoryItemMap.size();
    }

    @Override
    public void empty() {
        inventoryItemMap.clear();
    }
}
