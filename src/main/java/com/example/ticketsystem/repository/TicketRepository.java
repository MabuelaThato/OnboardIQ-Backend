package com.example.ticketsystem.repository;

import com.example.ticketsystem.App;
import com.example.ticketsystem.model.Ticket;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TicketRepository {
    private final String dbUrl = "jdbc:sqlite:tickets.db"; // Adjust path as needed

    public TicketRepository() {
        initializeDatabase();
    }

    private void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(dbUrl);
             Statement stmt = conn.createStatement()) {
            String sql = """
                CREATE TABLE IF NOT EXISTS tickets (
                    id TEXT PRIMARY KEY,
                    acquisionerName TEXT NOT NULL,
                    transactionerName TEXT NOT NULL,
                    clientInfo TEXT NOT NULL,
                    ibsNumber TEXT,
                    status TEXT NOT NULL,
                    createdAt TIMESTAMP NOT NULL
                )
                """;
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Database initialization failed", e);
        }
    }

    public Ticket save(Ticket ticket) {
        String sql = "INSERT INTO tickets (id, acquisionerName, transactionerName, clientInfo, ibsNumber, status, createdAt) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ticket.getId().toString());
            pstmt.setString(2, ticket.getAcquisionerName());
            pstmt.setString(3, ticket.getTransactionerName());
            pstmt.setString(4, ticket.getClientInfo());
            pstmt.setString(5, ticket.getIbsNumber());
            pstmt.setString(6, ticket.getStatus());
            pstmt.setTimestamp(7, ticket.getCreatedAt());
            pstmt.executeUpdate();
            return ticket; // Return the input ticket as saved
        } catch (SQLException e) {
            System.err.println("Failed to save ticket: " + e.getMessage());
            e.printStackTrace();
            return null; // Current behavior; consider throwing an exception instead
        }
    }

    public Ticket update(Ticket ticket) {
        String sql = "UPDATE tickets SET ibsNumber = ?, status = ? WHERE id = ?";
        try (Connection conn = App.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ticket.getIbsNumber());
            pstmt.setString(2, ticket.getStatus());
            pstmt.setString(3, ticket.getId().toString());
            pstmt.executeUpdate();
            return ticket;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Ticket findById(UUID id) {
        String sql = "SELECT * FROM tickets WHERE id = ?";
        try (Connection conn = App.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id.toString());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapToTicket(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Ticket> findByUserAndPeriod(String userEmail, String period) {
        List<Ticket> tickets = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate;
        switch (period) {
            case "today":
                startDate = now.truncatedTo(ChronoUnit.DAYS);
                break;
            case "last30":
                startDate = now.minusDays(30);
                break;
            case "last365":
                startDate = now.minusDays(365);
                break;
            default:
                throw new IllegalArgumentException("Invalid period");
        }
        Timestamp startTimestamp = Timestamp.valueOf(startDate);
        Timestamp endTimestamp = Timestamp.valueOf(now);

        String sql = "SELECT * FROM tickets WHERE (acquisioner_name = ? OR transactioner_name = ?) " +
                "AND created_at BETWEEN ? AND ?";
        try (Connection conn = App.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userEmail);
            pstmt.setString(2, userEmail);
            pstmt.setTimestamp(3, startTimestamp);
            pstmt.setTimestamp(4, endTimestamp);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                tickets.add(mapToTicket(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tickets;
    }

    private Ticket mapToTicket(ResultSet rs) throws SQLException {
        Ticket ticket = new Ticket();
        ticket.setId(UUID.fromString(rs.getString("id")));
        ticket.setAcquisionerName(rs.getString("acquisionerName"));
        ticket.setTransactionerName(rs.getString("transactionerName"));
        ticket.setClientInfo(rs.getString("clientInfo"));
        ticket.setIbsNumber(rs.getString("ibsNumber"));
        ticket.setStatus(rs.getString("status"));
        ticket.setCreatedAt(rs.getTimestamp("createdAt"));
        return ticket;
    }
}