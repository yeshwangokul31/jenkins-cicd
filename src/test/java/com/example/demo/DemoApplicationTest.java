
package com.example.demo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DemoApplicationTest {

    @Test
    void applicationShouldReturnHealthyStatus() {
        DemoApplication app = new DemoApplication();

        assertEquals("Application is healthy", app.health());
    }
}
