package com.ticketty.tickettyapp.controller.request;

import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.Pattern;

@Setter
@Getter
public class WinnerStatusUpdateRequest {

    @Pattern(regexp = "REQUEST_COMPLETED|PAYMENT_COMPLETED", message = "Invalid status value")
    private String status;

}
