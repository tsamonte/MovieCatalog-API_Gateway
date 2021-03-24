package edu.uci.ics.tsamonte.service.gateway.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SessionRequestModel {
    @JsonProperty(value = "email", required = true)
    private String email;

    @JsonProperty(value = "session_id", required = true)
    private String session_id;

    @JsonCreator
    public SessionRequestModel(@JsonProperty(value = "email", required = true) String email,
                               @JsonProperty(value = "session_id", required = true) String session_id) {
        this.email = email;
        this.session_id = session_id;
    }

    @JsonProperty(value = "email")
    public String getEmail() {
        return email;
    }

    @JsonProperty(value = "session_id")
    public String getSession_id() {
        return session_id;
    }
}
