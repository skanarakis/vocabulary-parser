package edu.teikav.robot.parser.util;

import edu.teikav.robot.parser.exceptions.CorruptedTrieException;

import java.util.*;

// Non-Thread Safe
public class Trie<E> {

    private static class TrieNode<E> {
        boolean isWord;
        E element;
        Map<Character, TrieNode<E>> children;

        TrieNode(E element) {
            this.element = element;
            children = new TreeMap<>();
        }

        TrieNode() {
            this(null);
        }
    }

    private TrieNode<E> root;
    private int trieSize;
    private int nodesSize;

    /**
     * Constructs a new Trie with zero size
     */
    public Trie() {
        this.root = new TrieNode<>();
        trieSize = 0;
        nodesSize = 1;
    }

    /**
     * Inserts a new element in the Trie based on given {@code key}
     * @param key Cannot be null. Used for specifying insertion location
     * @param element Inserted in the location specified by {@code key}
     */
    public void insert(String key, E element) {
        if (key == null) throw new IllegalArgumentException("Key cannot be null");
        root = insert(root, key, element, 0);
    }

    /**
     * Deletes an element from Trie based on given {@code key}
     * @param key Cannot be null. Used for searching for deletion
     */
    public void delete(String key) {
        if (key == null) throw new IllegalArgumentException("Key cannot be null");
        root = delete(root, key, 0);
    }

    /**
     * Deletes all elements in the Trie
     */
    public void deleteAllKeys() {
        for (String key : allKeys()) {
            delete(key);
        }
    }

    /**
     * Searches within Trie for an element based on given {@code key}
     * @param key Cannot be null. Used for searching
     * @return Element found or null if no element could be found
     */
    public E search(String key) {
        if (key == null) throw new IllegalArgumentException("Key cannot be null");
        TrieNode<E> node = search(root, key, 0);
        if (node == null) return null;
        if (!node.isWord)
            throw new CorruptedTrieException(String.format("%s is not marked as a stored item", node.element));
        return node.element;
    }

    /**
     * Returns all keys in the Trie. Can be iterated over with foreach
     * @return All keys in the Trie
     */
    public Iterable<String> allKeys() {
        return keysStartingWith("");
    }

    /**
     * Returns all of the keys in the Trie starting with {@code prefix}
     * @param prefix The prefix
     * @return All of the keys in the Trie starting with {@code prefix}
     */
    public Iterable<String> keysStartingWith(String prefix) {
        Queue<String> strings = new LinkedList<>();
        TrieNode<E> node = search(root, prefix, 0);
        collectKeys(node, new StringBuilder(prefix), strings);
        return strings;
    }

    /**
     * Returns all of elements in the Trie
     * @return All of the elements in the Trie
     */
    public Map<String, E> allElements() {
        return elementsOfKeysStartingWith("");
    }

    /**
     * Returns all of the elements in the Trie whose keys start with {@code prefix}
     * @param prefix The prefix
     * @return All of the elements in the Trie whose keys start with {@code prefix}
     */
    public Map<String,E> elementsOfKeysStartingWith(String prefix) {
        Map<String, E> elements = new LinkedHashMap<>();
        TrieNode<E> node = search(root, prefix, 0);
        collectElements(node, new StringBuilder(prefix), elements);
        return elements;
    }

    public boolean contains(String key) { return (search(key) != null); }

    public int size() { return trieSize; }

    public int internalSize(){ return nodesSize; }

    public boolean hasRoot() { return root != null; }

    public boolean isEmpty() { return trieSize == 0; }

    private TrieNode<E> insert(TrieNode<E> node, String key, E element, int index) {
        if (node == null) {
            node = new TrieNode<>();
            nodesSize++;
        }
        if (key.length() == index) {
            // TODO: overwriting right now. Later, we may want to update rather than overwriting
            node.element = element;
            if (!node.isWord) {
                trieSize++;
                node.isWord = true;
            }
            return node;
        }
        char c = key.charAt(index);
        TrieNode<E> next = node.children.get(c);
        node.children.put(c, insert(next, key, element, index + 1));
        return node;
    }

    private TrieNode<E> delete(TrieNode<E> node, String key, int index) {
        if (node == null) return null;
        if (key.length() == index) {
            if (node.element != null) {
                trieSize--;
                node.element = null;
            }
        }
        else {
            char c = key.charAt(index);
            TrieNode<E> next = node.children.get(c);
            node.children.put(c, delete(next, key, index + 1));
            if (node.children.get(c) == null) {
                node.children.remove(c);
            }
        }

        if (node.element == null && node.children.isEmpty()) {
            nodesSize--;
            return null;
        }
        return node;
    }

    private TrieNode<E> search(TrieNode<E> node, String key, int index) {
        if (node == null) return null;
        if (key.length() == index) return node;
        char c = key.charAt(index);
        return search(node.children.get(c), key, index + 1);
    }

    private void collectKeys(TrieNode<E> node, StringBuilder prefix, Queue<String> strings) {
        if (node == null) return;
        if (node.element != null) strings.add(prefix.toString());
        for (char c : node.children.keySet()) {
            // Search recursively all sorted (based on TreeMap) characters one by one
            prefix.append(c);
            TrieNode<E> next = node.children.get(c);
            collectKeys(next, prefix, strings);
            prefix.deleteCharAt(prefix.length() - 1);
        }
    }

    private void collectElements(TrieNode<E> node, StringBuilder prefix, Map<String, E> elements) {
        if (node == null) return;
        if (node.element != null) elements.put(prefix.toString(), node.element);
        for (char c : node.children.keySet()) {
            // Search recursively all sorted (based on TreeMap) characters one by one
            prefix.append(c);
            TrieNode<E> next = node.children.get(c);
            collectElements(next, prefix, elements);
            prefix.deleteCharAt(prefix.length() - 1);
        }
    }
}
