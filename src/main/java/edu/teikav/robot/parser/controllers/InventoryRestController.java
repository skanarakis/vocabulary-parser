package edu.teikav.robot.parser.controllers;

import edu.teikav.robot.parser.dtos.InventoryItemDTO;
import edu.teikav.robot.parser.dtos.InventoryItemsDTO;
import edu.teikav.robot.parser.services.InventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inventory")
public class InventoryRestController {

    private Logger logger = LoggerFactory.getLogger(InventoryRestController.class);

    private InventoryService inventoryService;

    @Autowired
    public InventoryRestController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping(value = "/terms", produces = "application/json")
    public InventoryItemsDTO getAllInventoryItems() {
        return new InventoryItemsDTO(inventoryService.getAllItems());
    }

    @GetMapping(value = "/terms/{term}", produces = "application/json")
    public InventoryItemDTO getInventoryItem(@PathVariable String term) {
        return new InventoryItemDTO(inventoryService.getItem(term));
    }
}
