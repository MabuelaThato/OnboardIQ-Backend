package com.example.ticketsystem;

import com.example.ticketsystem.controller.TicketController;
import io.javalin.Javalin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class App {
    private static final String DB_URL = "jdbc:sqlite:tickets.db";

    public static void main(String[] args) {
        // Create table if not exists
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS tickets (" +
                    "id TEXT PRIMARY KEY," +
                    "acquisioner_name TEXT NOT NULL," +
                    "transactioner_name TEXT NOT NULL," +
                    "client_info TEXT," +
                    "ibs_number TEXT," +
                    "status TEXT NOT NULL," +
                    "created_at TIMESTAMP NOT NULL" +
                    ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/static");
        }).start(7000);

        TicketController ticketController = new TicketController();
        ticketController.registerRoutes(app);
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
}