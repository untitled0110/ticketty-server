package com.ticketty.tickettyapp.controller.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AccountVerifyRequest {
    private String acctGb;
    private String bnkCd;
    private String acctNo;
    private String name;
}
