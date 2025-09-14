package com.example.ticketsystem.controller;

import com.example.ticketsystem.model.Ticket;
import com.example.ticketsystem.service.TicketService;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

public class TicketController {
    private final TicketService ticketService;

    public TicketController() {
        this.ticketService = new TicketService();
    }

    public void registerRoutes(Javalin app) {
        System.out.println("Registering routes");
        app.post("/tickets", this::createTicket);
        app.post("/tickets/{id}/update", this::updateIbsNumber);
        app.post("/send-tickets-email", this::sendTicketsEmail);
    }

    private void createTicket(Context ctx) {
        System.out.println("createTicket called");
        String acquisioner = ctx.formParam("acquisioner");
        String transactioner = ctx.formParam("transactioner");
        String clientInfo = ctx.formParam("clientInfo");

        if (acquisioner == null || transactioner == null || clientInfo == null) {
            ctx.status(400).result("Missing required form parameters");
            return;
        }

        try {
            Ticket ticket = ticketService.createTicket(acquisioner, transactioner, clientInfo);
            ctx.json(ticket);
        } catch (Exception e) {
            System.err.println("Error in createTicket: " + e.getMessage());
            e.printStackTrace();
            ctx.status(500).result("Internal server error: " + e.getMessage());
        }
    }


    private void updateIbsNumber(Context ctx) {
        UUID id = UUID.fromString(ctx.pathParam("id"));
        String ibsNumber = ctx.formParam("ibsNumber");
        Ticket updated = ticketService.updateIbsNumber(id, ibsNumber);
        if (updated != null) {
            ctx.json(updated);
        } else {
            ctx.status(400);
        }
    }

    private void sendTicketsEmail(Context ctx) {
        String userEmail = ctx.formParam("user");
        String period = ctx.formParam("period");
        ticketService.sendTicketsEmail(userEmail, period);
        ctx.status(200);
    }
}