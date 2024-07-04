package com.ticketty.tickettyapp.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TokenResponse {

    @JsonProperty("dataHeader")
    private DataHeader dataHeader;

    @JsonProperty("dataBody")
    private DataBody dataBody;

    @Data
    public static class DataHeader {
        @JsonProperty("GW_RSLT_CD")
        private String gwResultCode;

        @JsonProperty("GW_RSLT_MSG")
        private String gwResultMessage;
    }

    @Data
    public static class DataBody {
        @JsonProperty("access_token")
        private String accessToken;

        @JsonProperty("token_type")
        private String tokenType;

        @JsonProperty("expires_in")
        private Double expiresIn;

        @JsonProperty("scope")
        private String scope;
    }
}