package edu.uci.ics.tsamonte.service.gateway.resources;

import edu.uci.ics.tsamonte.service.gateway.GatewayService;
import edu.uci.ics.tsamonte.service.gateway.logger.ServiceLogger;
import edu.uci.ics.tsamonte.service.gateway.threadpool.ClientRequest;
import edu.uci.ics.tsamonte.service.gateway.threadpool.HTTPMethod;
import edu.uci.ics.tsamonte.service.gateway.transaction.TransactionGenerator;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("idm")
public class Idm {
    private static final String baseURI = GatewayService.getIdmConfigs().getScheme() +
            GatewayService.getIdmConfigs().getHostName() + ":" +
            GatewayService.getIdmConfigs().getPort() + GatewayService.getIdmConfigs().getPath();

    @Path("{all}")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response idmResponse(@Context HttpHeaders headers, byte[] jsonBytes, @PathParam("all") String endpoint) {
        try {
            ServiceLogger.LOGGER.info("idm/" + endpoint + " called");
            ClientRequest request = new ClientRequest();
            // Set fields in client
            request.setEmail("");

            // For all IDM endpoints, we do not need to call session
            request.setSession_id("");
            // Create a transaction_id
            String transaction_id = TransactionGenerator.generate();
            request.setTransaction_id(transaction_id);

            request.setURI(baseURI);

            String endpointPath = "";
            // set the correct endpoint
            switch (endpoint) {
                case "register":
                    endpointPath = GatewayService.getIdmConfigs().getRegisterPath();
                    break;
                case "login":
                    endpointPath = GatewayService.getIdmConfigs().getLoginPath();
                    break;
                case "session":
                    endpointPath = GatewayService.getIdmConfigs().getSessionPath();
                    break;
                case "privilege":
                    endpointPath = GatewayService.getIdmConfigs().getPrivilegePath();
                    break;
                default:
                    break;
            }
            request.setEndpoint(endpointPath);

            request.setMethod(HTTPMethod.POST);
            request.setRequestBytes(jsonBytes);
            request.setHeaders(null);
            request.setParams(null);
            // Place the request into a queue
            GatewayService.getThreadPool().putRequest(request);

            Response.ResponseBuilder builder = Response.noContent();
            builder.header("message", "Response is not ready");
            builder.header("request_delay", GatewayService.getThreadConfigs().getRequestDelay());
            builder.header("transaction_id", transaction_id);
            ServiceLogger.LOGGER.info("Sending empty response");
            return builder.build();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
