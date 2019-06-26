package edu.teikav.robot.parser.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class TokenUtilsTest {

    @Test
    public void degenerateTokenCases() {
        assertTrue(TokenUtils.isDegenerate(""));
        assertTrue(TokenUtils.isDegenerate("\\"));
        assertTrue(TokenUtils.isDegenerate("/"));
        assertTrue(TokenUtils.isDegenerate("~"));
        assertTrue(TokenUtils.isDegenerate("1"));
        assertTrue(TokenUtils.isDegenerate("a"));
    }

    @Test
    public void digitsOnlyTokenCases() {
        assertTrue(TokenUtils.isDigitsOnly("12345"));
        assertTrue(TokenUtils.isDigitsOnly("12.345"));
        assertTrue(TokenUtils.isDigitsOnly(".12345"));
        assertTrue(TokenUtils.isDigitsOnly("12345."));

        assertFalse(TokenUtils.isDigitsOnly("12345token"));
        assertFalse(TokenUtils.isDigitsOnly("12345 token"));
        assertFalse(TokenUtils.isDigitsOnly("1.2345 token"));
        assertFalse(TokenUtils.isDigitsOnly("1.2345  token"));
        assertFalse(TokenUtils.isDigitsOnly(".345 token"));
        assertFalse(TokenUtils.isDigitsOnly("12345.  token"));
    }

    @Test
    public void removeNumericDigitsBeforeTokenCases() {
        assertEquals("token",TokenUtils.removeNonLetterCharsInPrefix("1token"));
        assertEquals("token",TokenUtils.removeNonLetterCharsInPrefix("1.5 token"));
        assertEquals("token",TokenUtils.removeNonLetterCharsInPrefix("1.0token"));
        assertEquals("token",TokenUtils.removeNonLetterCharsInPrefix("1.token"));
        assertEquals("(token)",TokenUtils.removeNonLetterCharsInPrefix("1.(token)"));
        assertEquals("(v)",TokenUtils.removeNonLetterCharsInPrefix("(v)"));
        assertEquals("(v)",TokenUtils.removeNonLetterCharsInPrefix("2.3(v)"));
        assertEquals("...",TokenUtils.removeNonLetterCharsInPrefix("..."));
        assertEquals("a...b..",TokenUtils.removeNonLetterCharsInPrefix("a...b.."));
        assertEquals("a...b..",TokenUtils.removeNonLetterCharsInPrefix("234.5 a...b.."));
    }
}