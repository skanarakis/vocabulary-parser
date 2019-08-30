package edu.teikav.robot.parser.infrastructure;

import edu.teikav.robot.parser.util.TokenUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Unit-Test: Token Utilities")
class TokenUtilsTest {

    @Test
    void degenerateTokenCases() {
        Assertions.assertThat(TokenUtils.isDegenerate("")).isTrue();
        assertThat(TokenUtils.isDegenerate("\\")).isTrue();
        assertThat(TokenUtils.isDegenerate("/")).isTrue();
        assertThat(TokenUtils.isDegenerate("~")).isTrue();
        assertThat(TokenUtils.isDegenerate("1")).isTrue();
        assertThat(TokenUtils.isDegenerate("a")).isTrue();
    }

    @Test
    void digitsOnlyTokenCases() {
        assertThat(TokenUtils.isDigitsOnly("12345")).isTrue();
        assertThat(TokenUtils.isDigitsOnly("12.345")).isTrue();
        assertThat(TokenUtils.isDigitsOnly(".12345")).isTrue();
        assertThat(TokenUtils.isDigitsOnly("12345.")).isTrue();

        assertThat(TokenUtils.isDigitsOnly("12345token.")).isFalse();
        assertThat(TokenUtils.isDigitsOnly("12345 token.")).isFalse();
        assertThat(TokenUtils.isDigitsOnly("1.2345 token.")).isFalse();
        assertThat(TokenUtils.isDigitsOnly("1.2345  token")).isFalse();
        assertThat(TokenUtils.isDigitsOnly(".345 token")).isFalse();
        assertThat(TokenUtils.isDigitsOnly("12345.  token")).isFalse();
    }

    @Test
    void removeNumericDigitsBeforeTokenCases() {
        assertThat(TokenUtils.removeNonLetterCharsInPrefix("1token")).isEqualTo("token");
        assertThat(TokenUtils.removeNonLetterCharsInPrefix("1.5 token")).isEqualTo("token");
        assertThat(TokenUtils.removeNonLetterCharsInPrefix("1.5 token")).isEqualTo("token");
        assertThat(TokenUtils.removeNonLetterCharsInPrefix("1.token")).isEqualTo("token");
        assertThat(TokenUtils.removeNonLetterCharsInPrefix("1.(token)")).isEqualTo("(token)");
        assertThat(TokenUtils.removeNonLetterCharsInPrefix("(v)")).isEqualTo("(v)");
        assertThat(TokenUtils.removeNonLetterCharsInPrefix("2.3(v)")).isEqualTo("(v)");
        assertThat(TokenUtils.removeNonLetterCharsInPrefix("...")).isEqualTo("...");
        assertThat(TokenUtils.removeNonLetterCharsInPrefix("a...b..")).isEqualTo("a...b..");
        assertThat(TokenUtils.removeNonLetterCharsInPrefix("234.5 a...b..")).isEqualTo("a...b..");
    }
}