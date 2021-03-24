package edu.uci.ics.tsamonte.service.gateway.resources;

import edu.uci.ics.tsamonte.service.gateway.GatewayService;
import edu.uci.ics.tsamonte.service.gateway.core.MapCreator;
import edu.uci.ics.tsamonte.service.gateway.core.SessionCaller;
import edu.uci.ics.tsamonte.service.gateway.logger.ServiceLogger;
import edu.uci.ics.tsamonte.service.gateway.models.SessionRequestModel;
import edu.uci.ics.tsamonte.service.gateway.models.SessionResponseModel;
import edu.uci.ics.tsamonte.service.gateway.threadpool.ClientRequest;
import edu.uci.ics.tsamonte.service.gateway.threadpool.HTTPMethod;
import edu.uci.ics.tsamonte.service.gateway.transaction.TransactionGenerator;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.HashMap;

@Path("billing")
public class Billing {
    private static final String baseURI = GatewayService.getBillingConfigs().getScheme() +
            GatewayService.getBillingConfigs().getHostName() + ":" +
            GatewayService.getBillingConfigs().getPort() + GatewayService.getBillingConfigs().getPath();

    @Path("{first}/{second}")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postResponses(@Context HttpHeaders headers, byte[] jsonBytes,
                                  @PathParam("first") String firstPathParam,
                                  @PathParam("second") String secondPathParam) {
        ServiceLogger.LOGGER.info("Billing endpoint called");
        String email = headers.getHeaderString("email");
        String session_id = headers. getHeaderString("session_id");

        // Call idm/session endpoint to verify session
        ServiceLogger.LOGGER.info("Calling session with email: " + email + ", session_id: " + session_id);
        SessionRequestModel sessionRequest = new SessionRequestModel(email, session_id);
        Response response = SessionCaller.callSession(sessionRequest);
        SessionResponseModel sessionResponse = SessionCaller.createSessionResponseModel(response);

        // if session is not valid, return the response from the IDM
        if(sessionResponse.getResultCode() != 130) {
            return Response.status(response.getStatus()).entity(sessionResponse).build();
        }

        String transaction_id = TransactionGenerator.generate();

        HashMap<String, String> headerMap = MapCreator.createHeaderMap(email, session_id, transaction_id);

        String endpointPath = "";
        if(firstPathParam.equals("cart")) {
            switch (secondPathParam) {
                case "insert":
                    endpointPath = GatewayService.getBillingConfigs().getCartInsertPath();
                    break;
                case "update":
                    endpointPath = GatewayService.getBillingConfigs().getCartUpdatePath();
                    break;
                case "delete":
                    endpointPath = GatewayService.getBillingConfigs().getCartDeletePath();
                    break;
                case "retrieve":
                    endpointPath = GatewayService.getBillingConfigs().getCartRetrievePath();
                    break;
                case "clear":
                    endpointPath = GatewayService.getBillingConfigs().getCartClearPath();
                    break;
                default:
                    break;
            }
        }
        else if(firstPathParam.equals("order")) {
            switch (secondPathParam) {
                case "place":
                    endpointPath = GatewayService.getBillingConfigs().getOrderPlacePath();
                    break;
                case "retrieve":
                    endpointPath = GatewayService.getBillingConfigs().getOrderRetrievePath();
                    break;
                default:
                    break;
            }
        }

        ClientRequest request = new ClientRequest();
        request.setEmail(email);
        request.setSession_id(session_id);
        request.setTransaction_id(transaction_id);
        request.setURI(baseURI);
        request.setEndpoint(endpointPath);
        request.setMethod(HTTPMethod.POST);
        request.setRequestBytes(jsonBytes);
        request.setHeaders(headerMap);
        request.setParams(null);
        // Place the request into a queue
        GatewayService.getThreadPool().putRequest(request);

        Response.ResponseBuilder builder = Response.noContent();
        builder.header("message", "Response is not ready");
        builder.header("request_delay", GatewayService.getThreadConfigs().getRequestDelay());
        builder.header("transaction_id", transaction_id);
        return builder.build();
    }

    @Path("order/complete")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getResponse(@Context HttpHeaders headers, @Context UriInfo uriInfo) {
        ServiceLogger.LOGGER.info("Billing endpoint called");
        String email = headers.getHeaderString("email");
        String session_id = headers. getHeaderString("session_id");

        // Call idm/session endpoint to verify session
        SessionRequestModel sessionRequest = new SessionRequestModel(email, session_id);
        Response response = SessionCaller.callSession(sessionRequest);
        SessionResponseModel sessionResponse = SessionCaller.createSessionResponseModel(response);

        // if session is not valid, return the response from the IDM
        if(sessionResponse.getResultCode() != 130) {
            return Response.status(response.getStatus()).entity(sessionResponse).build();
        }

        String transaction_id = TransactionGenerator.generate();

        HashMap<String, String> headerMap = MapCreator.createHeaderMap(email, session_id, transaction_id);
        HashMap<String, String> paramsMap = MapCreator.createParamsMap(uriInfo.getQueryParameters());


        // Set client request variables
        ClientRequest request = new ClientRequest();
        request.setEmail(email);
        request.setSession_id(session_id);
        request.setTransaction_id(transaction_id);
        request.setURI(baseURI);
        request.setEndpoint(GatewayService.getBillingConfigs().getOrderCompletePath());

        request.setMethod(HTTPMethod.GET);
        request.setRequestBytes(null);
        request.setHeaders(headerMap);
        request.setParams(paramsMap);
        // Place the request into a queue
        GatewayService.getThreadPool().putRequest(request);

        Response.ResponseBuilder builder = Response.noContent();
        builder.header("message", "Response is not ready");
        builder.header("request_delay", GatewayService.getThreadConfigs().getRequestDelay());
        builder.header("transaction_id", transaction_id);
        return builder.build();
    }
}
