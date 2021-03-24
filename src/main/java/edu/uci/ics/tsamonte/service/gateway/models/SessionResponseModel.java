package edu.uci.ics.tsamonte.service.gateway.models;

import com.fasterxml.jackson.annotation.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SessionResponseModel {
    @JsonProperty(value = "resultCode", required = true)
    private int resultCode;

    @JsonProperty(value = "message", required = true)
    private String message;

    @JsonProperty(value = "session_id")
    private String session_id;


    @JsonCreator
    public SessionResponseModel(@JsonProperty(value = "resultCode", required = true) int resultCode,
                                @JsonProperty(value = "message", required = true) String message,
                                @JsonProperty(value = "session_id") String session_id) {
        this.resultCode = resultCode;
        this.message = message;
        this.session_id = session_id;
    }

    @JsonProperty(value = "resultCode")
    public int getResultCode() {
        return resultCode;
    }

    @JsonProperty(value = "message")
    public String getMessage() {
        return message;
    }

    @JsonProperty(value = "session_id")
    public String getSession_id() {
        return session_id;
    }
}
