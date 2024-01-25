package com.ticketty.tickettyapp.service.user;

import com.ticketty.tickettyapp.repository.user.UserJdbcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserJdbcRepository userJdbcRepository;

}
