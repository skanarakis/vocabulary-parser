package edu.teikav.robot.parser.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexTest {

    private Matcher matcher;

    @Test
    public void testGrammarTypeRegex_No1() {
        Pattern pattern = Pattern.compile("v|n|adj");

        matcher = pattern.matcher("v");
        Assert.assertTrue(matcher.matches());

        matcher = pattern.matcher("r");
        Assert.assertFalse(matcher.matches());

        matcher = pattern.matcher("n");
        Assert.assertTrue(matcher.matches());

        matcher = pattern.matcher("adj");
        Assert.assertTrue(matcher.matches());

        matcher = pattern.matcher("adj ");
        Assert.assertFalse(matcher.matches());
    }

    @Test
    public void testGrammarTypeRegex_No2() {
        Pattern pattern = Pattern.compile("^[v|n]\\s*");

        matcher = pattern.matcher("v");
        Assert.assertTrue(matcher.matches());

        matcher = pattern.matcher("n");
        Assert.assertTrue(matcher.matches());

        matcher = pattern.matcher("v ");
        Assert.assertTrue(matcher.matches());

        matcher = pattern.matcher("r");
        Assert.assertFalse(matcher.matches());

        matcher = pattern.matcher("av");
        Assert.assertFalse(matcher.matches());
    }

    @Test
    public void testGrammarTypeRegex_No3() {
        Pattern pattern = Pattern.compile("^\\(v\\)");

        matcher = pattern.matcher("(v)");
        Assert.assertTrue(matcher.matches());

        matcher = pattern.matcher("(n)");
        Assert.assertFalse(matcher.matches());

        matcher = pattern.matcher("v)");
        Assert.assertFalse(matcher.matches());

        matcher = pattern.matcher("a(v)");
        Assert.assertFalse(matcher.matches());
    }

    @Test
    public void testGrammarTypeRegex_No4() {
        Pattern pattern = Pattern.compile("(^\\(v\\)|^\\(n\\)|^\\(adj\\)).*");

        matcher = pattern.matcher("(v)");
        Assert.assertTrue(matcher.matches());

        matcher = pattern.matcher("(n)");
        Assert.assertTrue(matcher.matches());

        matcher = pattern.matcher("v)");
        Assert.assertFalse(matcher.matches());

        matcher = pattern.matcher("a(v)");
        Assert.assertFalse(matcher.matches());

        matcher = pattern.matcher("(adj)");
        Assert.assertTrue(matcher.matches());

        matcher = pattern.matcher("(v) (past: drove, past part: driven)");
        Assert.assertTrue(matcher.matches());
    }

    @Test
    public void testCompositeSplit() {
        String splitPattern = "\\s\\(";
        Assert.assertTrue("(v) (past: broke, past part: broken)".split(splitPattern).length == 2);
    }

    @Test
    public void findDerivativesPattern() {
        Pattern pattern = Pattern.compile("^.*(\\(n\\)|\\(v\\)).*$");

        matcher = pattern.matcher("text(n)");
        Assert.assertTrue(matcher.matches());

        matcher = pattern.matcher("text(v)");
        Assert.assertTrue(matcher.matches());

        matcher = pattern.matcher("text(no)");
        Assert.assertFalse(matcher.matches());
    }
}
