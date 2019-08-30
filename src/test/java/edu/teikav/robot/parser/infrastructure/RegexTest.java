package edu.teikav.robot.parser.infrastructure;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Unit-Test: Regular Expressions")
class RegexTest {

    private Matcher matcher;

    @Test
    void testGrammarTypeRegex_No1() {
        Pattern pattern = Pattern.compile("v|n|adj");

        matcher = pattern.matcher("v");
        assertThat(matcher.matches()).isTrue();

        matcher = pattern.matcher("r");
        assertThat(matcher.matches()).isFalse();

        matcher = pattern.matcher("n");
        assertThat(matcher.matches()).isTrue();

        matcher = pattern.matcher("adj");
        assertThat(matcher.matches()).isTrue();

        matcher = pattern.matcher("adj ");
        assertThat(matcher.matches()).isFalse();
    }

    @Test
    void testGrammarTypeRegex_No2() {
        Pattern pattern = Pattern.compile("^[v|n]\\s*");

        matcher = pattern.matcher("v");
        assertThat(matcher.matches()).isTrue();

        matcher = pattern.matcher("n");
        assertThat(matcher.matches()).isTrue();

        matcher = pattern.matcher("v ");
        assertThat(matcher.matches()).isTrue();

        matcher = pattern.matcher("r");
        assertThat(matcher.matches()).isFalse();

        matcher = pattern.matcher("av");
        assertThat(matcher.matches()).isFalse();
    }

    @Test
    void testGrammarTypeRegex_No3() {
        Pattern pattern = Pattern.compile("^\\(v\\)");

        matcher = pattern.matcher("(v)");
        assertThat(matcher.matches()).isTrue();

        matcher = pattern.matcher("(n)");
        assertThat(matcher.matches()).isFalse();

        matcher = pattern.matcher("v)");
        assertThat(matcher.matches()).isFalse();

        matcher = pattern.matcher("a(v)");
        assertThat(matcher.matches()).isFalse();
    }

    @Test
    void testGrammarTypeRegex_No4() {
        Pattern pattern = Pattern.compile("(^\\(v\\)|^\\(n\\)|^\\(adj\\)).*");

        matcher = pattern.matcher("(v)");
        assertThat(matcher.matches()).isTrue();

        matcher = pattern.matcher("(n)");
        assertThat(matcher.matches()).isTrue();

        matcher = pattern.matcher("v)");
        assertThat(matcher.matches()).isFalse();

        matcher = pattern.matcher("a(v)");
        assertThat(matcher.matches()).isFalse();

        matcher = pattern.matcher("(adj)");
        assertThat(matcher.matches()).isTrue();

        matcher = pattern.matcher("(v) (past: drove, past part: driven)");
        assertThat(matcher.matches()).isTrue();
    }

    @Test
    void testCompositeSplit() {
        String splitPattern = "\\s\\(";
        assertThat("(v) (past: broke, past part: broken)".split(splitPattern).length).isEqualTo(2);
    }

    @Test
    void findDerivativesPattern() {
        Pattern pattern = Pattern.compile("^.*(\\(n\\)|\\(v\\)).*$");

        matcher = pattern.matcher("text(n)");
        assertThat(matcher.matches()).isTrue();

        matcher = pattern.matcher("text(v)");
        assertThat(matcher.matches()).isTrue();

        matcher = pattern.matcher("text(no)");
        assertThat(matcher.matches()).isFalse();
    }
}
