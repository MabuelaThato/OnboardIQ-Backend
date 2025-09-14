package com.example.ticketsystem.service;

import com.example.ticketsystem.model.Ticket;
import com.example.ticketsystem.repository.TicketRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class TicketService {
    private final TicketRepository ticketRepository;
    private final EmailService emailService;

    public TicketService() {
        this.ticketRepository = new TicketRepository();
        this.emailService = new EmailService();
    }

    public Ticket createTicket(String acquisionerName, String transactionerName, String clientInfo) {
        try {
            Ticket ticket = new Ticket();
            ticket.setId(UUID.randomUUID());
            ticket.setAcquisionerName(acquisionerName);
            ticket.setTransactionerName(transactionerName);
            ticket.setClientInfo(clientInfo);
            ticket.setIbsNumber("");
            ticket.setStatus("active");
            ticket.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));

            Ticket savedTicket = ticketRepository.save(ticket);

            if (savedTicket == null) {
                throw new RuntimeException("Failed to save ticket to database");
            }

            // Send email to transactioner with form link
            String formLink = "http://localhost:7000/form.html?id=" + savedTicket.getId();
            String emailBody = "New ticket created: ID=" + savedTicket.getId() + "\nClient Info: " + clientInfo + "\nEnter IBS number here: " + formLink;
            emailService.sendEmail(transactionerName, "New Ticket", emailBody);

            return savedTicket;
        } catch (Exception e) {
            System.err.println("Error creating ticket: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to create ticket: " + e.getMessage(), e);
        }
    }

    public Ticket updateIbsNumber(UUID id, String ibsNumber) {
        Ticket ticket = ticketRepository.findById(id);
        if (ticket != null && "active".equals(ticket.getStatus())) {
            ticket.setIbsNumber(ibsNumber);
            ticket.setStatus("completed");
            Ticket updatedTicket = ticketRepository.update(ticket);

            // Send completion email to both
            String completionBody = "Ticket completed: ID=" + updatedTicket.getId() + "\nIBS: " + ibsNumber + "\nClient Info: " + updatedTicket.getClientInfo();
            emailService.sendEmail(updatedTicket.getAcquisionerName(), "Ticket Completed", completionBody);
            emailService.sendEmail(updatedTicket.getTransactionerName(), "Ticket Completed", completionBody);

            return updatedTicket;
        }
        return null;
    }

    public void sendTicketsEmail(String userEmail, String period) {
        List<Ticket> tickets = ticketRepository.findByUserAndPeriod(userEmail, period);

        StringBuilder pendingTable = new StringBuilder("<table><tr><th>ID</th><th>Status</th></tr>");
        StringBuilder completedTable = new StringBuilder("<table><tr><th>ID</th><th>IBS</th><th>Status</th></tr>");

        for (Ticket ticket : tickets) {
            if ("active".equals(ticket.getStatus())) {
                pendingTable.append("<tr><td>").append(ticket.getId()).append("</td><td>").append(ticket.getStatus()).append("</td></tr>");
            } else if ("completed".equals(ticket.getStatus())) {
                completedTable.append("<tr><td>").append(ticket.getId()).append("</td><td>").append(ticket.getIbsNumber()).append("</td><td>").append(ticket.getStatus()).append("</td></tr>");
            }
        }
        pendingTable.append("</table>");
        completedTable.append("</table>");

        String emailBody = "<h1>Pending Tickets</h1>" + pendingTable + "<h1>Completed Tickets</h1>" + completedTable;
        emailService.sendEmail(userEmail, "Your Tickets for " + period, emailBody);
    }
}