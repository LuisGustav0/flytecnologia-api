package com.example.demo;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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
class JUnit5ExampleTest {
    @Autowired
    private FlyStr matematica;

    @DisplayName("DisplayName justAnExample")
    @Test
    void justAnExample() {
        System.out.println("This test method should be run...........................s.");
    }

    @DisplayName("DisplayName testAbout")
    @Test
    void testAbout() {
        System.out.println("sssssssssssssssssssssssssssss.");
    }

    @BeforeAll
    static void beforeAll() {
        System.out.println("Before all test methods");
    }

    @BeforeEach
    void beforeEach() {
        System.out.println("Before each test method");
    }

    @AfterEach
    void afterEach() {
        System.out.println("After each test method");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("After all test methods");
    }

    @Test
    void testSoma() {
        int resultado = matematica.soma(1, 3);

        assertEquals(4, resultado);
    }
}
