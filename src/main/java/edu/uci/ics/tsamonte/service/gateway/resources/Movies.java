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

@Path("movies")
public class Movies {
    private static final String baseURI = GatewayService.getMoviesConfigs().getScheme() +
            GatewayService.getMoviesConfigs().getHostName() + ":" +
            GatewayService.getMoviesConfigs().getPort() + GatewayService.getMoviesConfigs().getPath();
    @Path("{all}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response onePathGet(@Context HttpHeaders headers, @Context UriInfo uriInfo,
                                   @PathParam("all") String endpoint) {
        ServiceLogger.LOGGER.info("Movie endpoint called");
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

        System.out.println("================STARTING PARAM PRINT====================");
        for(String key : paramsMap.keySet()) {
            System.out.println(key + ": " + paramsMap.get(key));
        }
        System.out.println("========================================================");

        // Set client request variables
        ClientRequest request = new ClientRequest();
        request.setEmail(email);
        request.setSession_id(session_id);
        request.setTransaction_id(transaction_id);
        request.setURI(baseURI);

        String endpointPath = "";
        switch(endpoint) {
            case "search":
                endpointPath = GatewayService.getMoviesConfigs().getSearchPath();
                break;
            case "people":
                endpointPath = GatewayService.getMoviesConfigs().getPeoplePath();
                break;
            default:
                break;
        }
        request.setEndpoint(endpointPath);

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

    @Path("{first}/{second}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response twoPathGet(@Context HttpHeaders headers, @Context UriInfo uriInfo,
                                   @PathParam("first") String firstPathParam,
                                   @PathParam("second") String secondPathParam) {
        ServiceLogger.LOGGER.info("Movie endpoint called");
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

        String endpointPath = "";
        switch (firstPathParam) {
            case "browse":
                endpointPath = GatewayService.getMoviesConfigs().getBrowsePath() + secondPathParam;
                break;
            case "get":
                endpointPath = GatewayService.getMoviesConfigs().getGetPath() + secondPathParam;
                break;
            case "people":
                endpointPath = GatewayService.getMoviesConfigs().getPeopleSearchPath();
                break;
            default:
                break;
        }
        request.setEndpoint(endpointPath);

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

    @Path("people/get/{person_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response threePathGet(@Context HttpHeaders headers, @Context UriInfo uriInfo,
                                   @PathParam("person_id") String person_id) {
        ServiceLogger.LOGGER.info("Movie endpoint called");
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
        request.setEndpoint(GatewayService.getMoviesConfigs().getPeopleGetPath() + person_id);

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

    @Path("thumbnail")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response onePathPost(@Context HttpHeaders headers, @Context UriInfo uriInfo,
                                byte[] jsonBytes) {
        ServiceLogger.LOGGER.info("Movie endpoint called");
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
        ClientRequest request = new ClientRequest(email, session_id, transaction_id, baseURI,
                GatewayService.getMoviesConfigs().getThumbnailPath(), HTTPMethod.POST, jsonBytes,
                headerMap, paramsMap);

        // Place the request into a queue
        GatewayService.getThreadPool().putRequest(request);

        Response.ResponseBuilder builder = Response.noContent();
        builder.header("message", "Response is not ready");
        builder.header("request_delay", GatewayService.getThreadConfigs().getRequestDelay());
        builder.header("transaction_id", transaction_id);
        // TODO: pass in requestModel.getSessionId (new session id) through the headers
        return builder.build();
    }
}
