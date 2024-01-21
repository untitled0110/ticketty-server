package com.ticketty.tickettyapp.service.user;

import com.ticketty.tickettyapp.dto.user.UserCreateRequest;
import com.ticketty.tickettyapp.repository.user.UserJdbcRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserJdbcRepository userJdbcRepository;

    public UserService(UserJdbcRepository userJdbcRepository) {
        this.userJdbcRepository = userJdbcRepository;
    }

    public void saveUser(UserCreateRequest request) {
        userJdbcRepository.saveUser(request.getEmail(), request.getPassword());
    }

}
