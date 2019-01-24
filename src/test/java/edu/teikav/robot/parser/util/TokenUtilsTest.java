package edu.teikav.robot.parser.util;

import static org.junit.Assert.*;

import org.junit.Test;

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
        assertEquals("token",TokenUtils.removeNumericDigitsBeforeToken("1token"));
        assertEquals("token",TokenUtils.removeNumericDigitsBeforeToken("1.5 token"));
        assertEquals("token",TokenUtils.removeNumericDigitsBeforeToken("1.0token"));
        assertEquals("token",TokenUtils.removeNumericDigitsBeforeToken("1.token"));
        assertEquals("(token)",TokenUtils.removeNumericDigitsBeforeToken("1.(token)"));
        assertEquals("(v)",TokenUtils.removeNumericDigitsBeforeToken("(v)"));
        assertEquals("(v)",TokenUtils.removeNumericDigitsBeforeToken("2.3(v)"));
        assertEquals("...",TokenUtils.removeNumericDigitsBeforeToken("..."));
        assertEquals("a...b..",TokenUtils.removeNumericDigitsBeforeToken("a...b.."));
        assertEquals("a...b..",TokenUtils.removeNumericDigitsBeforeToken("234.5 a...b.."));
    }
}