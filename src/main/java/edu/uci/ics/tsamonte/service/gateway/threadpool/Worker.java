package edu.uci.ics.tsamonte.service.gateway.threadpool;

import edu.uci.ics.tsamonte.service.gateway.GatewayService;
import edu.uci.ics.tsamonte.service.gateway.core.EndpointCaller;
import edu.uci.ics.tsamonte.service.gateway.core.GatewayRecords;

import javax.ws.rs.core.Response;

public class Worker extends Thread {
    int id;
    ThreadPool threadPool;

    private ClientRequest request;

    private Worker(int id, ThreadPool threadPool) {
        this.id = id;
        this.threadPool = threadPool;
    }

    public static Worker CreateWorker(int id, ThreadPool threadPool) {
        return new Worker(id, threadPool);
    }

    public void process() {
//        request = threadPool.takeRequest();
    }

    @Override
    public void run() {
        while (true) {
//            System.out.println("Worker was awakened");
            request = threadPool.takeRequest();
            System.out.println("Request has been taken");
//            connection = GatewayService.getConnectionPoolManager().requestCon();
            Response response = EndpointCaller.callEndpoint(request);

            // insert response, email, session_id, and transaction_id into db
            // Maybe do this in EndpointCaller?
//            GatewayRecords.insertIntoResponses(request.getTransaction_id(), request.getEmail(),
//                    request.getSession_id(), response.readEntity(String.class), response.getStatus(), connection);
            // Do you only do the ConnectionPool stuff with the gateway db? or do you have to implement it
            // in the endpoint calls too?

//            GatewayService.getConnectionPoolManager().releaseCon(connection);
        }
    }
}
