package com.example.ticketsystem;

import com.example.ticketsystem.model.Ticket;
import com.example.ticketsystem.service.TicketService;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class TicketServiceTest {
    private TicketService ticketService = new TicketService();

    @Test
    void testCreateTicket() {
        // Clear table for test
        clearTable();

        Ticket ticket = ticketService.createTicket("acq@example.com", "trans@example.com", "Client info");
        assertNotNull(ticket.getId());
        assertEquals("active", ticket.getStatus());
        assertEquals("", ticket.getIbsNumber());
    }

    @Test
    void testUpdateIbsNumber() {
        // Clear table for test
        clearTable();

        Ticket ticket = ticketService.createTicket("acq@example.com", "trans@example.com", "Client info");
        Ticket updated = ticketService.updateIbsNumber(ticket.getId(), "IBS123");
        assertEquals("IBS123", updated.getIbsNumber());
        assertEquals("completed", updated.getStatus());
    }

    private void clearTable() {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:tickets.db");
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM tickets");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void testTicketRepositorySave() {
        TicketService service = new TicketService();
        Ticket saved = service.createTicket("trans@example.com", "test@example.com", "test info");
        assertNotNull(saved);
    }
}