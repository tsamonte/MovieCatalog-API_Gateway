package edu.uci.ics.tsamonte.service.gateway.core;

import edu.uci.ics.tsamonte.service.gateway.GatewayService;
import edu.uci.ics.tsamonte.service.gateway.logger.ServiceLogger;
import edu.uci.ics.tsamonte.service.gateway.threadpool.ClientRequest;
import edu.uci.ics.tsamonte.service.gateway.threadpool.HTTPMethod;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Connection;

public class EndpointCaller {
    public static Response callEndpoint(ClientRequest request) {
        // create a client
        ServiceLogger.LOGGER.info("Building client...");
        Client client = ClientBuilder.newClient();
        client.register(JacksonFeature.class);

        // Create a WebTarget to send a request at
        ServiceLogger.LOGGER.info("Building WebTarget..."  + request.getURI() + request.getEndpoint());
        WebTarget webTarget = client.target(request.getURI()).path(request.getEndpoint());

        // add params to webTarget if available
        if(request.getParams() != null) {
            for (String key : request.getParams().keySet()) {
                webTarget = webTarget.queryParam(key, request.getParams().get(key));
            }
        }

        // Create an InvocationBuilder to create the HTTP request
        ServiceLogger.LOGGER.info("Starting invocation builder");
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

        // add headers to invocationBuilder if available
        if(request.getHeaders() != null) {
            for (String key : request.getHeaders().keySet()) {
                invocationBuilder = invocationBuilder.header(key, request.getHeaders().get(key));
            }
        }

        // Send the request and save it to a response
        ServiceLogger.LOGGER.info("Sending request...");
        Response response;
        if(request.getMethod().equals(HTTPMethod.GET)) {
            response = invocationBuilder.get();
        }
        else if(request.getMethod().equals(HTTPMethod.POST)) {
            // Ask how to send as byte[]
            response = invocationBuilder.post(Entity.entity(request.getRequestBytes(), MediaType.APPLICATION_JSON));
        }
        else {
            response = null;
        }
        ServiceLogger.LOGGER.info("Request sent.");

        ServiceLogger.LOGGER.info("Received status " + response.getStatus());

        // Insert into database
        Connection connection = GatewayService.getConnectionPoolManager().requestCon();

        GatewayRecords.insertIntoResponses(request.getTransaction_id(), request.getEmail(),
                request.getSession_id(), response.readEntity(String.class), response.getStatus(), connection);

        GatewayService.getConnectionPoolManager().releaseCon(connection);

        // Status code: response.getStatus()
        // Object: String jsonText = response.readEntity(String.class)
        return response;
    }
}
