package edu.teikav.robot.parser.controllers;

import edu.teikav.robot.parser.domain.InventoryItem;
import edu.teikav.robot.parser.domain.SpeechPart;
import edu.teikav.robot.parser.services.InventoryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(InventoryRestController.class)
public class InventoryRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InventoryService inventoryService;

    @Test
    public void retrieveSingleInventoryItem() throws Exception {

        String term = "testTerm";

        InventoryItem item = new InventoryItem(term);
        item.setTermType(SpeechPart.NOUN);
        item.setTranslation("some translation");
        item.setExample("some example");

        Mockito.when(inventoryService.getItem(term)).thenReturn(item);
        mockMvc.perform(get("/inventory/terms/" + term))
                .andExpect(status().isOk())
                .andExpect(jsonPath("speechPart", equalTo("NOUN")))
                .andExpect(jsonPath("translation", equalTo("some translation")))
                .andExpect(jsonPath("example", equalTo("some example")));
    }

    @Test
    public void retrieveAllInventoryItems() throws Exception {

        String term1 = "testTerm1";
        String term2 = "testTerm2";

        InventoryItem item1 = new InventoryItem(term1);
        item1.setTermType(SpeechPart.NOUN);
        item1.setTranslation("some translation 1");
        item1.setExample("some example 1");
        InventoryItem item2 = new InventoryItem(term2);
        item2.setTermType(SpeechPart.VERB);
        item2.setTranslation("some translation 2");
        item2.setExample("some example 2");
        Map<String, InventoryItem> mockedItems = new HashMap<>();
        mockedItems.put(term1, item1);
        mockedItems.put(term2, item2);

        Mockito.when(inventoryService.getAllItems()).thenReturn(mockedItems);
        mockMvc.perform(get("/inventory/terms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vocabularyInventory." + term1 + ".speechPart",
                        equalTo(SpeechPart.NOUN.toString())))
                .andExpect(jsonPath("$.vocabularyInventory." + term1 + ".translation",
                        equalTo("some translation 1")))
                .andExpect(jsonPath("$.vocabularyInventory." + term1 + ".example",
                        equalTo("some example 1")))
                .andExpect(jsonPath("$.vocabularyInventory." + term2 + ".speechPart",
                        equalTo(SpeechPart.VERB.toString())))
                .andExpect(jsonPath("$.vocabularyInventory." + term2 + ".translation",
                        equalTo("some translation 2")))
                .andExpect(jsonPath("$.vocabularyInventory." + term2 + ".example",
                        equalTo("some example 2")));
    }

    @TestConfiguration
    static class InventoryRestControllerTestConfig {
        @Bean
        InventoryService getInventoryService() {
            return Mockito.mock(InventoryService.class);
        }
    }
}