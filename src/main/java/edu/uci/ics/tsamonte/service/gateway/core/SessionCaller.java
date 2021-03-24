package edu.uci.ics.tsamonte.service.gateway.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.tsamonte.service.gateway.GatewayService;
import edu.uci.ics.tsamonte.service.gateway.logger.ServiceLogger;
import edu.uci.ics.tsamonte.service.gateway.models.SessionRequestModel;
import edu.uci.ics.tsamonte.service.gateway.models.SessionResponseModel;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

public class SessionCaller {
    public static Response callSession(SessionRequestModel requestModel) {
        // Create path variables
        String servicePath = GatewayService.getIdmConfigs().getScheme() +
                GatewayService.getIdmConfigs().getHostName() + ":" +
                GatewayService.getIdmConfigs().getPort() + GatewayService.getIdmConfigs().getPath();
        String endpointPath = GatewayService.getIdmConfigs().getSessionPath();

        // Create a client
        ServiceLogger.LOGGER.info("Building client...");
        Client client = ClientBuilder.newClient();
        client.register(JacksonFeature.class);

        // Create a WebTarget to send a request at
        ServiceLogger.LOGGER.info("Building WebTarget...");
        WebTarget webTarget = client.target(servicePath).path(endpointPath);

        // Create an InvocationBuilder to create the HTTP request
        ServiceLogger.LOGGER.info("Starting invocation builder...");
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
        invocationBuilder = invocationBuilder.header("email", requestModel.getEmail());
        invocationBuilder = invocationBuilder.header("session_id", requestModel.getSession_id());

        // Send the request and save it to a response
        ServiceLogger.LOGGER.info("Sending request...");
        Response response = invocationBuilder.post(Entity.entity(requestModel, MediaType.APPLICATION_JSON));
        ServiceLogger.LOGGER.info("Request sent.");

        ServiceLogger.LOGGER.info("Received status " + response.getStatus());
        return response;
    }

    public static SessionResponseModel createSessionResponseModel(Response response) {
        SessionResponseModel responseModel = null;

        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonText = response.readEntity(String.class);
            responseModel = mapper.readValue(jsonText, SessionResponseModel.class);
            // insert into status
//            responseModel.setStatus(response.getStatus());
            ServiceLogger.LOGGER.info("Successfully mapped response to POJO");
        }
        catch (IOException e) {
            ServiceLogger.LOGGER.warning("Unable to map response to POJO");
        }

        return responseModel;
    }
}
