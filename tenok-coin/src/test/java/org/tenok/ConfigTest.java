package org.tenok;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.tenok.coin.config.ConfigParser;

public class ConfigTest {
    @Test
    public void configParserTest() {
        assertEquals(1000, ConfigParser.getUpdateRate());
    }
}
