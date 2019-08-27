package com.flytecnologia.core.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static com.flytecnologia.core.util.FlyString.decapitalizeFirstLetter;

class FlyStringUTest {

    @Test
    void decapitalizeFirstLetterTest() {
        String name = decapitalizeFirstLetter("Jullierme");

        assertEquals("jullierme", name);

        name = decapitalizeFirstLetter("");

        assertEquals("", name);

        name = decapitalizeFirstLetter(null);

        assertEquals("", name);
    }
}
