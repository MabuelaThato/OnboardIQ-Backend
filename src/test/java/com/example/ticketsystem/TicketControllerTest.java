package com.example.ticketsystem;

import com.example.ticketsystem.controller.TicketController;

import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TicketControllerTest {
    TicketController controller = new TicketController();

    @Test
    public void testCreateTicketEndpoint() {
        Javalin app = Javalin.create();
        controller.registerRoutes(app);

        JavalinTest.test(app, (server, client) -> {
            var response = client.post("/tickets", "acquisioner=acq@example.com&transactioner=trans@example.com&clientInfo=info");
            assertEquals(200, response.code());
            assertTrue(response.body().string().contains("\"status\":\"active\""));
        });
    }
}