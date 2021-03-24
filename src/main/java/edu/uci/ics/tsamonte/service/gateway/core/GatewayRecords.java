package edu.uci.ics.tsamonte.service.gateway.core;

import edu.uci.ics.tsamonte.service.gateway.GatewayService;
import edu.uci.ics.tsamonte.service.gateway.logger.ServiceLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GatewayRecords {
    public static void insertIntoResponses(String transaction_id, String email, String session_id,
                                           String response, int status, Connection connection) {
        try {
            String insertStatement = "INSERT INTO responses" +
                    " (transaction_id, email, session_id, response, http_status)" +
                    " VALUES (?, ?, ?, ?, ?);";
            PreparedStatement ps = connection.prepareStatement(insertStatement);
            ps.setString(1, transaction_id);
            ps.setString(2, email);
            ps.setString(3, session_id);
            ps.setString(4, response);
            ps.setInt(5, status);

            ServiceLogger.LOGGER.info("Trying insert: " + ps.toString());
            ps.executeUpdate();
            ServiceLogger.LOGGER.info("Insert successful.");
        }
        catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Insert failed: Unable to insert into responses table");
            e.printStackTrace();
        }
    }

    public static void deleteFromResponses(String transaction_id, Connection connection) {
        try {
            String deleteStatement = "DELETE FROM responses WHERE transaction_id = ?;";
            PreparedStatement ps = connection.prepareStatement(deleteStatement);
            ps.setString(1, transaction_id);

            ServiceLogger.LOGGER.info("Trying delete: " + ps.toString());
            ps.executeUpdate();
            ServiceLogger.LOGGER.info("Delete successful.");
        }
        catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Delete failed: Unable to delete from responses table");
            e.printStackTrace();
        }
    }

    public static ResultSet queryFromResponses(String transaction_id, Connection connection) {
        try {
            String query = "SELECT *" +
                    " FROM responses" +
                    " WHERE transaction_id = ?;";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, transaction_id);

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query successful.");

            return rs;
        }
        catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Query failed: Unable to retrieve from responses table");
            e.printStackTrace();
            return null;
        }
    }
}
