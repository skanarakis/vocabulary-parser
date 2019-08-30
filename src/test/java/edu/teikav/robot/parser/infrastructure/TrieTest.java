package edu.teikav.robot.parser.infrastructure;

import edu.teikav.robot.parser.domain.InventoryItem;
import edu.teikav.robot.parser.domain.SpeechPart;
import edu.teikav.robot.parser.util.Trie;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Unit-Test: Inventory Trie")
class TrieTest {

    @Test
    void createSingleNodeTrieAndSearchTheSame() {
        Trie<String> trie = new Trie<>();
        trie.insert("Hi", "World");
        assertEquals("World", trie.search("Hi"));
    }

    @Test
    void createEmptyTrie() {
        Trie<String> trie = new Trie<>();
        assertThat(trie.isEmpty()).isTrue();
    }

    @Test
    void createSingleNodeTrieAndSearchForAnother() {
        Trie<String> trie = new Trie<>();
        trie.insert("Hi", "World");
        assertThat(trie.search("Bye")).isNull();
    }

    @Test
    void createTrieAndSearchForAlmostTheSame() {
        Trie<String> trie = new Trie<>();
        trie.insert("Helpful", "Bingo");

        assertThat(trie.search("Helpful")).isEqualTo("Bingo");
        assertThat(trie.search("Helping")).isNull();
    }

    @Test
    void createTrieWithSimilarEntries() {
        Trie<String> trie = new Trie<>();
        trie.insert("Helpful", "Bingo1");
        trie.insert("Helpfully", "Bingo2");

        assertThat(trie.search("Helpful")).isEqualTo("Bingo1");
        assertThat(trie.search("Helpfully")).isEqualTo("Bingo2");
        assertThat(trie.search("HelpFully")).isNull();
    }

    @Test
    void createTrieSmallerEntriesInsertedLast() {
        Trie<String> trie = new Trie<>();
        trie.insert("Helpful", "Bingo1");
        trie.insert("Help", "Bingo2");

        assertThat(trie.search("Helpful")).isEqualTo("Bingo1");
        assertThat(trie.search("Help")).isEqualTo("Bingo2");
    }

    @Test
    void exerciseTrie_1() {
        Trie<String> trie = new Trie<>();
        trie.insert("Hi", "1");
        trie.insert("be", "2");
        trie.insert("at", "3");
        trie.insert("on", "4");

        assertThat(trie.search("Hi")).isEqualTo("1");
        assertThat(trie.search("be")).isEqualTo("2");
        assertThat(trie.search("at")).isEqualTo("3");
        assertThat(trie.search("on")).isEqualTo("4");
        assertThat(trie.search("the")).isNull();
    }

    @Test
    void exerciseTrie_2() {
        Trie<String> trie = new Trie<>();
        trie.insert("configure", "ρυθμίζω");
        trie.insert("lighthouse", "φάρος");
        trie.insert("mean", "εννοώ");
        trie.insert("danger", "κίνδυνος");
        trie.insert("round", "στρογγυλός");
        trie.insert("shine", "φωτίζω");
        trie.insert("automatic", "αυτόματος");
        trie.insert("mark", "βαθμός");
        trie.insert("come", "έρχομαι");

        assertThat(trie.search("configure")).isEqualTo("ρυθμίζω");
        assertThat(trie.search("lighthouse")).isEqualTo("φάρος");
        assertThat(trie.search("mean")).isEqualTo("εννοώ");
        assertThat(trie.search("danger")).isEqualTo("κίνδυνος");
        assertThat(trie.search("round")).isEqualTo("στρογγυλός");
        assertThat(trie.search("shine")).isEqualTo("φωτίζω");
        assertThat(trie.search("automatic")).isEqualTo("αυτόματος");
        assertThat(trie.search("mark")).isEqualTo("βαθμός");
        assertThat(trie.search("come")).isEqualTo("έρχομαι");
    }

    @Test
    void exerciseTrie_3() {
        Trie<String> trie = new Trie<>();
        trie.insert("Hi", "1");
        trie.insert("Come on", "2");
        trie.insert("The", "3");
        trie.insert("It", "4");
        trie.insert("Itself", "5");
        trie.insert("There", "6");
        trie.insert("Hit", "7");

        assertThat(trie.search("Hit")).isEqualTo("7");
        assertThat(trie.search("There")).isEqualTo("6");
        assertThat(trie.search("Itself")).isEqualTo("5");
        assertThat(trie.search("It")).isEqualTo("4");
        assertThat(trie.search("The")).isEqualTo("3");
        assertThat(trie.search("Come on")).isEqualTo("2");
        assertThat(trie.search("Hi")).isEqualTo("1");
        assertThat(trie.search("the")).isNull();
        assertThat(trie.search("it")).isNull();
    }

    @Test
    void exerciseTrieContains() {
        Trie<String> trie = new Trie<>();
        trie.insert("Hi", "1");
        trie.insert("Come on", "2");
        trie.insert("North Atlantic", "3");
        trie.insert("Keep on", "4");
        trie.insert("Itself", "5");
        trie.insert("It", "6");
        trie.insert("There", "7");
        trie.insert("Hit", "8");

        assertThat(trie.contains("Hi")).isTrue();
        assertThat(trie.contains("Come on")).isTrue();
        assertThat(trie.contains("North Atlantic")).isTrue();
        assertThat(trie.contains("Keep on")).isTrue();
        assertThat(trie.contains("Itself")).isTrue();
        assertThat(trie.contains("It")).isTrue();
        assertThat(trie.contains("There")).isTrue();
        assertThat(trie.contains("Hit")).isTrue();
        assertThat(trie.contains("hit")).isFalse();
        assertThat(trie.contains("bye")).isFalse();
    }

    @Test
    void exerciseTrieSize() {
        Trie<String> trie = new Trie<>();
        trie.insert("Hi", "1");
        trie.insert("Come on", "2");
        trie.insert("North Atlantic", "3");
        trie.insert("Keep on", "4");
        trie.insert("Itself", "5");
        trie.insert("It", "6");
        trie.insert("There", "7");
        trie.insert("Hit", "8");
        assertThat(trie.size()).isEqualTo(8);
    }

    @Test
    void exerciseTrieDeletions() {
        Trie<String> trie = new Trie<>();
        trie.insert("Hi", "1");
        trie.insert("Come on", "2");
        trie.insert("North Atlantic", "3");
        trie.insert("Keep on", "4");
        trie.insert("Itself", "5");
        trie.insert("It", "6");
        trie.insert("There", "7");
        trie.insert("Hit", "8");

        assertThat(trie.contains("Hi")).isTrue();
        assertThat(trie.contains("Come on")).isTrue();
        assertThat(trie.contains("North Atlantic")).isTrue();
        assertThat(trie.contains("Keep on")).isTrue();
        assertThat(trie.contains("Itself")).isTrue();
        assertThat(trie.contains("It")).isTrue();
        assertThat(trie.contains("There")).isTrue();
        assertThat(trie.contains("Hit")).isTrue();
        assertThat(trie.size()).isEqualTo(8);

        trie.delete("Hi");
        assertThat(trie.contains("Hi")).isFalse();
        assertThat(trie.contains("Come on")).isTrue();
        assertThat(trie.contains("North Atlantic")).isTrue();
        assertThat(trie.contains("Keep on")).isTrue();
        assertThat(trie.contains("Itself")).isTrue();
        assertThat(trie.contains("It")).isTrue();
        assertThat(trie.contains("There")).isTrue();
        assertThat(trie.contains("Hit")).isTrue();
        assertThat(trie.size()).isEqualTo(7);

        trie.delete("Come on");
        assertThat(trie.contains("Hi")).isFalse();
        assertThat(trie.contains("Come on")).isFalse();
        assertThat(trie.contains("North Atlantic")).isTrue();
        assertThat(trie.contains("Keep on")).isTrue();
        assertThat(trie.contains("Itself")).isTrue();
        assertThat(trie.contains("It")).isTrue();
        assertThat(trie.contains("There")).isTrue();
        assertThat(trie.contains("Hit")).isTrue();
        assertThat(trie.size()).isEqualTo(6);

        trie.delete("North Atlantic");
        assertThat(trie.contains("Hi")).isFalse();
        assertThat(trie.contains("Come on")).isFalse();
        assertThat(trie.contains("North Atlantic")).isFalse();
        assertThat(trie.contains("Keep on")).isTrue();
        assertThat(trie.contains("Itself")).isTrue();
        assertThat(trie.contains("It")).isTrue();
        assertThat(trie.contains("There")).isTrue();
        assertThat(trie.contains("Hit")).isTrue();
        assertThat(trie.size()).isEqualTo(5);

        trie.delete("Keep on");
        trie.delete("Itself");
        assertThat(trie.contains("Hi")).isFalse();
        assertThat(trie.contains("Come on")).isFalse();
        assertThat(trie.contains("North Atlantic")).isFalse();
        assertThat(trie.contains("Keep on")).isFalse();
        assertThat(trie.contains("Itself")).isFalse();
        assertThat(trie.contains("It")).isTrue();
        assertThat(trie.contains("There")).isTrue();
        assertThat(trie.contains("Hit")).isTrue();
        assertThat(trie.size()).isEqualTo(3);

        trie.delete("It");
        trie.delete("There");
        trie.delete("Hit");
        assertThat(trie.contains("Hi")).isFalse();
        assertThat(trie.contains("Come on")).isFalse();
        assertThat(trie.contains("North Atlantic")).isFalse();
        assertThat(trie.contains("Keep on")).isFalse();
        assertThat(trie.contains("Itself")).isFalse();
        assertThat(trie.contains("It")).isFalse();
        assertThat(trie.contains("There")).isFalse();
        assertThat(trie.contains("Hit")).isFalse();
        assertThat(trie.size()).isEqualTo(0);
    }

    @Test
    void fetchAllKeysInSortedOrder() {
        Trie<String> trie = new Trie<>();
        trie.insert("Hi", "Hi");
        trie.insert("Come on", "Come on");
        trie.insert("North Atlantic", "North Atlantic");
        trie.insert("Keep on", "Keep on");
        trie.insert("Itself", "Itself");
        trie.insert("It", "It");
        trie.insert("There", "There");
        trie.insert("Hit", "Hit");
        Iterable<String> results = trie.allKeys();

        Iterator<String> it = results.iterator();
        assertThat(it.next()).isEqualTo("Come on");
        assertThat(it.next()).isEqualTo("Hi");
        assertThat(it.next()).isEqualTo("Hit");
        assertThat(it.next()).isEqualTo("It");
        assertThat(it.next()).isEqualTo("Itself");
        assertThat(it.next()).isEqualTo("Keep on");
        assertThat(it.next()).isEqualTo("North Atlantic");
        assertThat(it.next()).isEqualTo("There");
    }

    @Test
    void fetchAllElementsInSortedOrderOfKeys() {
        Trie<String> trie = new Trie<>();
        trie.insert("Hi", "1");
        trie.insert("Come on", "2");
        trie.insert("North Atlantic", "3");
        trie.insert("Keep on", "4");
        trie.insert("Itself", "5");
        trie.insert("It", "6");
        trie.insert("There", "7");
        trie.insert("Hit", "8");
        Map<String, String> elements = trie.allElements();

        Iterator<String> it = elements.values().iterator();
        assertThat(it.next()).isEqualTo("2");
        assertThat(it.next()).isEqualTo("1");
        assertThat(it.next()).isEqualTo("8");
        assertThat(it.next()).isEqualTo("6");
        assertThat(it.next()).isEqualTo("5");
        assertThat(it.next()).isEqualTo("4");
        assertThat(it.next()).isEqualTo("3");
        assertThat(it.next()).isEqualTo("7");
    }

    @Test
    void deleteCompleteTrie() {
        Trie<String> trie = new Trie<>();
        trie.insert("Hi", "1");
        trie.insert("Come on", "2");
        trie.insert("North Atlantic", "3");
        trie.insert("Keep on", "4");
        trie.insert("Itself", "5");
        trie.insert("It", "6");
        trie.insert("There", "7");
        trie.insert("Hit", "8");

        assertEquals(8, trie.size());
        trie.deleteAllKeys();
        assertEquals(0, trie.size());
        assertThat(trie.contains("Hi")).isFalse();
        assertThat(trie.contains("Come on")).isFalse();
        assertThat(trie.contains("North Atlantic")).isFalse();
        assertThat(trie.contains("Keep on")).isFalse();
        assertThat(trie.contains("Itself")).isFalse();
        assertThat(trie.contains("It")).isFalse();
        assertThat(trie.contains("There")).isFalse();
        assertThat(trie.contains("Hit")).isFalse();
    }

    @Test
    void updateEntries() {
        Trie<String> trie = new Trie<>();
        trie.insert("Hi", "1");
        trie.insert("Come on", "2");
        trie.insert("North Atlantic", "3");
        trie.insert("Keep on", "4");
        trie.insert("Itself", "5");
        trie.insert("It", "6");
        trie.insert("There", "7");
        trie.insert("Hit", "8");

        assertThat(trie.size()).isEqualTo(8);
        trie.insert("Hi", "11");
        trie.insert("Hit", "12");
        assertThat(trie.size()).isEqualTo(8);
        assertThat(trie.search("Hi")).isEqualTo("11");
        assertThat(trie.search("Hit")).isEqualTo("12");
    }

    @Test
    void fetchSubsetOfKeys() {
        Trie<String> trie = new Trie<>();
        trie.insert("Hi", "1");
        trie.insert("Come on", "2");
        trie.insert("North Atlantic", "3");
        trie.insert("Keep on", "4");
        trie.insert("Itself", "5");
        trie.insert("It", "6");
        trie.insert("There", "7");
        trie.insert("Hit", "8");

        assertThat(trie.size()).isEqualTo(8);
        Iterable<String> results = trie.keysStartingWith("I");

        Iterator<String> it = results.iterator();
        assertThat(it.next()).isEqualTo("It");
        assertThat(it.next()).isEqualTo("Itself");
    }

    @Test
    void fetchSubsetOfElements() {
        Trie<String> trie = new Trie<>();
        trie.insert("Hi", "1");
        trie.insert("Come on", "2");
        trie.insert("North Atlantic", "3");
        trie.insert("Keep on", "4");
        trie.insert("Itself", "5");
        trie.insert("It", "6");
        trie.insert("There", "7");
        trie.insert("Hit", "8");

        assertThat(trie.size()).isEqualTo(8);
        Map<String, String> results = trie.elementsOfKeysStartingWith("I");

        Iterator<String> it = results.values().iterator();
        assertThat(it.next()).isEqualTo("6");
        assertThat(it.next()).isEqualTo("5");
    }

    @Test
    void buildBigTrie() {
        Trie<Integer> trie = new Trie<>();
        Random random = new Random();
        final int SIZE = 220000;
        String[] keys = new String[SIZE];
        for (int i = 0; i < SIZE; i++) {
            keys[i] = RandomStringUtils.random(2 + random.nextInt(13), true, false);
        }

        long start = System.nanoTime();
        for (int i = 0; i < SIZE; i++) {
            trie.insert(keys[i], i + 1);
        }
        long end = System.nanoTime();

        System.out.println("Time elapsed for insertion: " + ((end - start)/1_000) + " microseconds");
        System.out.println("Trie size: " + trie.size());
        System.out.println("Number of nodes: " + trie.internalSize());
    }

    @Test
    void exerciseNodesSize1() {
        Trie<String> trie = new Trie<>();
        trie.insert("babe", "1");
        trie.insert("babel", "2");

        assertThat(trie.size()).isEqualTo(2);
        assertThat(trie.internalSize()).isEqualTo(6);
    }

    @Test
    void exerciseNodesSize2() {
        Trie<String> trie = new Trie<>();
        trie.insert("age", "1");
        trie.insert("fear", "2");
        trie.insert("fold", "3");

        assertThat(trie.size()).isEqualTo(3);
        assertThat(trie.internalSize()).isEqualTo(11);
    }

    @Test
    void exerciseNodesSize3() {
        Trie<String> trie = new Trie<>();
        trie.insert("be", "1");
        trie.insert("beware", "2");
        trie.insert("bed", "3");
        trie.insert("beside", "4");
        trie.insert("besides", "5");
        trie.insert("floor", "6");
        trie.insert("flu", "7");

        assertThat(trie.size()).isEqualTo(7);
        assertThat(trie.internalSize()).isEqualTo(19);
    }

    @Test
    void exerciseNodesSize4() {
        Trie<String> trie = new Trie<>();
        trie.insert("foo", "1");
        trie.insert("fear", "2");

        assertThat(trie.size()).isEqualTo(2);
        assertThat(trie.internalSize()).isEqualTo(7);
        trie.delete("foo");
        assertThat(trie.size()).isEqualTo(1);
        assertThat(trie.internalSize()).isEqualTo(5);
    }

    @Test
    void exerciseDelete() {
        Trie<String> trie = new Trie<>();
        trie.insert("food", "1");
        trie.delete("food");
        assertThat(trie.hasRoot()).isFalse();
    }

    @Test
    void deleteBigTrie() {
        Trie<Integer> trie = new Trie<>();
        Random random = new Random();
        final int SIZE = 1000;
        String[] keys = new String[SIZE];
        for (int i = 0; i < SIZE; i++) {
            keys[i] = RandomStringUtils.random(2 + random.nextInt(13), true, false);
        }

        for (int i = 0; i < SIZE; i++) {
            trie.insert(keys[i], i + 1);
        }

        trie.deleteAllKeys();

        assertThat(trie.size()).isEqualTo(0);
        assertThat(trie.internalSize()).isEqualTo(0);
    }

    @Test
    void buildBigTrieWithRealInventory() {
        Trie<InventoryItem> trie = new Trie<>();
        Random random = new Random();
        final int SIZE = 100000;
        String[] keys = new String[SIZE];
        InventoryItem[] items = new InventoryItem[SIZE];
        long start, end;
        start = System.nanoTime();
        for (int i = 0; i < SIZE; i++) {
            keys[i] = RandomStringUtils.random(2 + random.nextInt(13), true, false);
            items[i] = new InventoryItem.Builder(keys[i])
                    .ofType(SpeechPart.NOUN)
                    .pronouncedAs("pronunciation" + i)
                    .havingExample("Some example will follow. This is a medium long example for term " + i)
                    .translatedAs("This is the translation for " + i)
                    .withDerivate("derivative for " + i)
                    .withOpposite("opposite for " + i)
                    .havingSynonym("synonyms for " + i)
                    .build();
        }
        end = System.nanoTime();
        System.out.println("Time elapsed for construction: " + ((end - start)/1_000) + " microseconds");

        start = System.nanoTime();
        for (int i = 0; i < SIZE; i++) {
            trie.insert(keys[i], items[i]);
        }
        end = System.nanoTime();

        System.out.println("Time elapsed for insertion: " + ((end - start)/1_000) + " microseconds");
        System.out.println("Trie size: " + trie.size());
        System.out.println("Number of nodes: " + trie.internalSize());
    }

    @Test
    void exercisePrefixes() {
        Trie<Integer> trie = new Trie<>();
        trie.insert("future", 1);
        trie.insert("fuel", 2);
        trie.insert("infrastructure", 3);
        trie.insert("infuriated", 4);
        trie.insert("infant", 5);
        trie.insert("leak", 6);
        trie.insert("leap", 7);

        Iterator<String> it = trie.keysStartingWith("fu").iterator();
        assertThat(it.next()).isEqualTo("fuel");
        assertThat(it.next()).isEqualTo("future");

        it = trie.keysStartingWith("inf").iterator();
        assertThat(it.next()).isEqualTo("infant");
        assertThat(it.next()).isEqualTo("infrastructure");
        assertThat(it.next()).isEqualTo("infuriated");

        it = trie.keysStartingWith("le").iterator();
        assertThat(it.next()).isEqualTo("leak");
        assertThat(it.next()).isEqualTo("leap");

        trie.deleteAllKeys();
        it = trie.keysStartingWith("le").iterator();
        assertThat(it.hasNext()).isFalse();
    }

}