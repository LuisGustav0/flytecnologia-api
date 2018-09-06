package com.flytecnologia.core.util;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
class FlyStringTest {

    @Test
    void decapitalizeFirstLetterTest() {
        String name = FlyString.decapitalizeFirstLetter("Jullierme");

        assertEquals("jullierme", name);

        name = FlyString.decapitalizeFirstLetter("");

        assertEquals("", name);

        name = FlyString.decapitalizeFirstLetter(null);

        assertEquals("", name);
    }
}
