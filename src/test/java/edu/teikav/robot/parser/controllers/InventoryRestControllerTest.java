package edu.teikav.robot.parser.controllers;

import edu.teikav.robot.parser.domain.InventoryItem;
import edu.teikav.robot.parser.domain.SpeechPart;
import edu.teikav.robot.parser.services.InventoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = InventoryRestController.class)
@DisplayName("Slice-Test: Inventory REST Controller")
class InventoryRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InventoryService inventoryService;

    @Test
    void retrieveSingleInventoryItem() throws Exception {

        String term = "testTerm";
        String translation = "some translation";
        String example = "some example";
        SpeechPart speechPart = SpeechPart.NOUN;

        InventoryItem item = new InventoryItem.Builder(term)
                .ofType(speechPart)
                .translatedAs(translation)
                .havingExample(example)
                .build();

        Mockito.when(inventoryService.getItem(term)).thenReturn(item);
        mockMvc.perform(get("/inventory/terms/" + term))
                .andExpect(status().isOk())
                .andExpect(jsonPath("speechPart", equalTo(speechPart.toString())))
                .andExpect(jsonPath("translation", equalTo(translation)))
                .andExpect(jsonPath("example", equalTo(example)));
    }

    @Test
    void retrieveAllInventoryItems() throws Exception {

        String term1 = "testTerm1";
        String translation1 = "some translation 1";
        String example1 = "some example 1";
        SpeechPart speechPart1 = SpeechPart.NOUN;

        String term2 = "testTerm2";
        String translation2 = "some translation 2";
        String example2 = "some example 2";
        SpeechPart speechPart2 = SpeechPart.VERB;

        InventoryItem item1 = new InventoryItem.Builder(term1)
                .ofType(speechPart1)
                .translatedAs(translation1)
                .havingExample(example1)
                .build();
        InventoryItem item2 = new InventoryItem.Builder(term2)
                .ofType(speechPart2)
                .translatedAs(translation2)
                .havingExample(example2)
                .build();

        Map<String, InventoryItem> mockedItems = new HashMap<>();
        mockedItems.put(term1, item1);
        mockedItems.put(term2, item2);

        Mockito.when(inventoryService.getAllItems()).thenReturn(mockedItems);
        mockMvc.perform(get("/inventory/terms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vocabularyInventory." + term1 + ".speechPart",
                        equalTo(speechPart1.toString())))
                .andExpect(jsonPath("$.vocabularyInventory." + term1 + ".translation",
                        equalTo(translation1)))
                .andExpect(jsonPath("$.vocabularyInventory." + term1 + ".example",
                        equalTo(example1)))
                .andExpect(jsonPath("$.vocabularyInventory." + term2 + ".speechPart",
                        equalTo(speechPart2.toString())))
                .andExpect(jsonPath("$.vocabularyInventory." + term2 + ".translation",
                        equalTo(translation2)))
                .andExpect(jsonPath("$.vocabularyInventory." + term2 + ".example",
                        equalTo(example2)));
    }
}