package com.flytecnologia.core.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@DisplayName("JUnit 5 Example")
class FlyStringTest {
    @Autowired
    private FlyString flyString;

    @Test
    void decapitalizeFirstLetterTest() {
        String name = FlyString.decapitalizeFirstLetter("Jullierme");

        assertEquals("jullierme", name);
    }
}
