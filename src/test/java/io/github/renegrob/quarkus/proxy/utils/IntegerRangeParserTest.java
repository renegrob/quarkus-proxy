package io.github.renegrob.quarkus.proxy.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IntegerRangeParserTest {

    private IntegerRangeParser parser = IntegerRangeParser.INSTANCE;

    @Test
    void shouldParseSingleInt() {
        assertThat(parser.parseRange("65535"))
                .isEqualTo(new int[]{65535});
    }

    @Test
    void shouldParseList() {
        assertThat(parser.parseRange("1,2,12"))
                .isEqualTo(new int[]{1,2,12});
    }

    @Test
    void shouldParseRange() {
        assertThat(parser.parseRange("10-20"))
                .isEqualTo(new int[]{10,11,12,13,14,15,16,17,18,19,20});
    }

    @Test
    void shouldParseMixed() {
        assertThat(parser.parseRange("1, 5-9, 23"))
                .isEqualTo(new int[]{1,5,6,7,8,9,23});
    }
}