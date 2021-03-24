package edu.uci.ics.tsamonte.service.gateway.resources;

import edu.uci.ics.tsamonte.service.gateway.GatewayService;
import edu.uci.ics.tsamonte.service.gateway.core.GatewayRecords;
import edu.uci.ics.tsamonte.service.gateway.core.SessionCaller;
import edu.uci.ics.tsamonte.service.gateway.logger.ServiceLogger;
import edu.uci.ics.tsamonte.service.gateway.models.SessionRequestModel;
import edu.uci.ics.tsamonte.service.gateway.models.SessionResponseModel;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

@Path("report")
public class Report {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response reportResponse(@Context HttpHeaders headers) {
        ServiceLogger.LOGGER.info("Report endpoint called");
        Connection connection = GatewayService.getConnectionPoolManager().requestCon();
        try {
            String email = headers.getHeaderString("email");
            String session_id = headers.getHeaderString("session_id");
            String transaction_id = headers.getHeaderString("transaction_id");

//            // Call idm/session endpoint to verify session
//            if(session_id != null) {
//                SessionRequestModel sessionRequest = new SessionRequestModel(email, session_id);
//                ServiceLogger.LOGGER.info("Created session request with email: " + email + ", sid: " + session_id + ", tid: " + transaction_id);
//                Response response = SessionCaller.callSession(sessionRequest);
//                ServiceLogger.LOGGER.info("Response: " + response.toString());
//                SessionResponseModel sessionResponse = SessionCaller.createSessionResponseModel(response);
//                session_id = sessionResponse.getSession_id();
//
//
//                // if session is not valid, return the response from the IDM
//                if (sessionResponse.getResultCode() != 130) {
//                    Response.ResponseBuilder builder = Response.status(response.getStatus()).entity(sessionResponse);
//                    builder.header("email", email);
//                    builder.header("session_id", session_id);
//                    builder.header("transaction_id", transaction_id);
//                    return builder.build();
//                }
//            }

            ResultSet rs = GatewayRecords.queryFromResponses(transaction_id, connection);

            if (rs != null && rs.next()) {
                // create response with headers
                int http_status = rs.getInt("http_status");
                String jsonBody = rs.getString("response");
                Response.ResponseBuilder builder = Response.status(http_status).entity(jsonBody);
                builder.header("email", rs.getString("email"));
                builder.header("session_id", rs.getString("session_id"));
                builder.header("transaction_id", transaction_id);
                builder.header("session_id", session_id);

                // delete corresponding db record
                GatewayRecords.deleteFromResponses(transaction_id, connection);

                GatewayService.getConnectionPoolManager().releaseCon(connection);
                return builder.build();
            } else {
                // send the no content response;
                Response.ResponseBuilder builder = Response.noContent();
                builder.header("message", "Response is not ready");
                builder.header("request_delay", GatewayService.getThreadConfigs().getRequestDelay());
                builder.header("transaction_id", transaction_id);
                builder.header("session_id", session_id);
                GatewayService.getConnectionPoolManager().releaseCon(connection);
                return builder.build();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            GatewayService.getConnectionPoolManager().releaseCon(connection);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}
