package edu.teikav.robot.parser.util;

import org.junit.Test;

import java.util.Iterator;
import java.util.Map;

import static org.junit.Assert.*;

public class TrieTest {

    @Test
    public void createSingleNodeTrieAndSearchTheSame() {
        Trie<String> trie = new Trie<>();
        trie.insert("Hi", "World");
        assertEquals("World", trie.search("Hi"));
    }

    @Test
    public void createEmptyTrie() {
        Trie<String> trie = new Trie<>();
        assertTrue(trie.isEmpty());
    }

    @Test
    public void createSingleNodeTrieAndSearchForAnother() {
        Trie<String> trie = new Trie<>();
        trie.insert("Hi", "World");
        assertNull(trie.search("Bye"));
    }

    @Test
    public void createTrieAndSearchForAlmostTheSame() {
        Trie<String> trie = new Trie<>();
        trie.insert("Helpful", "Bingo");

        assertEquals("Bingo", trie.search("Helpful"));
        assertNull(trie.search("Helping"));
    }

    @Test
    public void createTrieWithSimilarEntries() {
        Trie<String> trie = new Trie<>();
        trie.insert("Helpful", "Bingo1");
        trie.insert("Helpfully", "Bingo2");

        assertEquals("Bingo1", trie.search("Helpful"));
        assertEquals("Bingo2", trie.search("Helpfully"));
        assertNull(trie.search("HelpFully"));
    }

    @Test
    public void createTrieSmallerEntriesInsertedLast() {
        Trie<String> trie = new Trie<>();
        trie.insert("Helpful", "Bingo1");
        trie.insert("Help", "Bingo2");

        assertEquals("Bingo1", trie.search("Helpful"));
        assertEquals("Bingo2", trie.search("Help"));
    }

    @Test
    public void exerciseTrie_1() {
        Trie<String> trie = new Trie<>();
        trie.insert("Hi", "1");
        trie.insert("be", "2");
        trie.insert("at", "3");
        trie.insert("on", "4");

        assertEquals("1", trie.search("Hi"));
        assertEquals("2", trie.search("be"));
        assertEquals("3", trie.search("at"));
        assertEquals("4", trie.search("on"));
        assertNull(trie.search("the"));
    }

    @Test
    public void exerciseTrie_2() {
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

        assertEquals("ρυθμίζω", trie.search("configure"));
        assertEquals("φάρος", trie.search("lighthouse"));
        assertEquals("εννοώ", trie.search("mean"));
        assertEquals("κίνδυνος", trie.search("danger"));
        assertEquals("στρογγυλός", trie.search("round"));
        assertEquals("φωτίζω", trie.search("shine"));
        assertEquals("αυτόματος", trie.search("automatic"));
        assertEquals("βαθμός", trie.search("mark"));
        assertEquals("έρχομαι", trie.search("come"));
    }

    @Test
    public void exerciseTrie_3() {
        Trie<String> trie = new Trie<>();
        trie.insert("Hi", "1");
        trie.insert("Come on", "2");
        trie.insert("The", "3");
        trie.insert("It", "4");
        trie.insert("Itself", "5");
        trie.insert("There", "6");
        trie.insert("Hit", "7");

        assertEquals("7", trie.search("Hit"));
        assertEquals("6", trie.search("There"));
        assertEquals("5", trie.search("Itself"));
        assertEquals("4", trie.search("It"));
        assertEquals("3", trie.search("The"));
        assertEquals("2", trie.search("Come on"));
        assertEquals("1", trie.search("Hi"));
        assertNull(trie.search("the"));
        assertNull(trie.search("it"));
    }

    @Test
    public void exerciseTrieContains() {
        Trie<String> trie = new Trie<>();
        trie.insert("Hi", "1");
        trie.insert("Come on", "2");
        trie.insert("North Atlantic", "3");
        trie.insert("Keep on", "4");
        trie.insert("Itself", "5");
        trie.insert("It", "6");
        trie.insert("There", "7");
        trie.insert("Hit", "8");

        assertTrue(trie.contains(("Hi")));
        assertTrue(trie.contains(("Come on")));
        assertTrue(trie.contains(("North Atlantic")));
        assertTrue(trie.contains(("Keep on")));
        assertTrue(trie.contains(("Itself")));
        assertTrue(trie.contains(("It")));
        assertTrue(trie.contains(("There")));
        assertTrue(trie.contains(("Hit")));
        assertFalse(trie.contains(("hit")));
        assertFalse(trie.contains(("bye")));
    }

    @Test
    public void exerciseTrieSize() {
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
    }

    @Test
    public void exerciseTrieDeletions() {
        Trie<String> trie = new Trie<>();
        trie.insert("Hi", "1");
        trie.insert("Come on", "2");
        trie.insert("North Atlantic", "3");
        trie.insert("Keep on", "4");
        trie.insert("Itself", "5");
        trie.insert("It", "6");
        trie.insert("There", "7");
        trie.insert("Hit", "8");

        assertTrue(trie.contains(("Hi")));
        assertTrue(trie.contains(("Come on")));
        assertTrue(trie.contains(("North Atlantic")));
        assertTrue(trie.contains(("Keep on")));
        assertTrue(trie.contains(("Itself")));
        assertTrue(trie.contains(("It")));
        assertTrue(trie.contains(("There")));
        assertTrue(trie.contains(("Hit")));
        assertEquals(8, trie.size());

        trie.delete("Hi");
        assertFalse(trie.contains(("Hi")));
        assertTrue(trie.contains(("Come on")));
        assertTrue(trie.contains(("North Atlantic")));
        assertTrue(trie.contains(("Keep on")));
        assertTrue(trie.contains(("Itself")));
        assertTrue(trie.contains(("It")));
        assertTrue(trie.contains(("There")));
        assertTrue(trie.contains(("Hit")));
        assertEquals(7, trie.size());

        trie.delete("Come on");
        assertFalse(trie.contains(("Hi")));
        assertFalse(trie.contains(("Come on")));
        assertTrue(trie.contains(("North Atlantic")));
        assertTrue(trie.contains(("Keep on")));
        assertTrue(trie.contains(("Itself")));
        assertTrue(trie.contains(("It")));
        assertTrue(trie.contains(("There")));
        assertTrue(trie.contains(("Hit")));
        assertEquals(6, trie.size());

        trie.delete("North Atlantic");
        assertFalse(trie.contains(("Hi")));
        assertFalse(trie.contains(("Come on")));
        assertFalse(trie.contains(("North Atlantic")));
        assertTrue(trie.contains(("Keep on")));
        assertTrue(trie.contains(("Itself")));
        assertTrue(trie.contains(("It")));
        assertTrue(trie.contains(("There")));
        assertTrue(trie.contains(("Hit")));
        assertEquals(5, trie.size());

        trie.delete("Keep on");
        trie.delete("Itself");
        assertFalse(trie.contains(("Hi")));
        assertFalse(trie.contains(("Come on")));
        assertFalse(trie.contains(("North Atlantic")));
        assertFalse(trie.contains(("Keep on")));
        assertFalse(trie.contains(("Itself")));
        assertTrue(trie.contains(("It")));
        assertTrue(trie.contains(("There")));
        assertTrue(trie.contains(("Hit")));
        assertEquals(3, trie.size());

        trie.delete("It");
        trie.delete("There");
        trie.delete("Hit");
        assertFalse(trie.contains(("Hi")));
        assertFalse(trie.contains(("Come on")));
        assertFalse(trie.contains(("North Atlantic")));
        assertFalse(trie.contains(("Keep on")));
        assertFalse(trie.contains(("Itself")));
        assertFalse(trie.contains(("It")));
        assertFalse(trie.contains(("There")));
        assertFalse(trie.contains(("Hit")));
        assertEquals(0, trie.size());
    }

    @Test
    public void fetchAllKeysInSortedOrder() {
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
        assertEquals("Come on", it.next());
        assertEquals("Hi", it.next());
        assertEquals("Hit", it.next());
        assertEquals("It", it.next());
        assertEquals("Itself", it.next());
        assertEquals("Keep on", it.next());
        assertEquals("North Atlantic", it.next());
        assertEquals("There", it.next());
    }

    @Test
    public void fetchAllElementsInSortedOrderOfKeys() {
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
        assertEquals("2", it.next());
        assertEquals("1", it.next());
        assertEquals("8", it.next());
        assertEquals("6", it.next());
        assertEquals("5", it.next());
        assertEquals("4", it.next());
        assertEquals("3", it.next());
        assertEquals("7", it.next());
    }

    @Test
    public void deleteCompleteTrie() {
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
        assertFalse(trie.contains("Hi"));
        assertFalse(trie.contains("Come on"));
        assertFalse(trie.contains("North Atlantic"));
        assertFalse(trie.contains("Keep on"));
        assertFalse(trie.contains("Itself"));
        assertFalse(trie.contains("It"));
        assertFalse(trie.contains("There"));
        assertFalse(trie.contains("Hit"));
    }

    @Test
    public void updateEntries() {
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
        trie.insert("Hi", "11");
        trie.insert("Hit", "12");
        assertEquals(8, trie.size());
        assertEquals("11", trie.search("Hi"));
        assertEquals("12", trie.search("Hit"));
    }

    @Test
    public void fetchSubsetOfKeys() {
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
        Iterable<String> results = trie.keysStartingWith("I");

        Iterator<String> it = results.iterator();
        assertEquals("It", it.next());
        assertEquals("Itself", it.next());
    }

    @Test
    public void fetchSubsetOfElements() {
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
        Map<String, String> results = trie.elementsOfKeysStartingWith("I");

        Iterator<String> it = results.values().iterator();
        assertEquals("6", it.next());
        assertEquals("5", it.next());
    }
}