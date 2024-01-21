package com.ticketty.tickettyapp.repository.user;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;


@Repository
public class UserJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean isUserNotExist(String email){
        String readSql = "SELECT * FROM user WHERE email = ?";
        return jdbcTemplate.query(readSql, (rs, rowNum) -> 0, email).isEmpty();
    }

    public void saveUser(String email, String password) {
        String sql = "INSERT INTO user(email, password) VALUES(?, ?)";
        jdbcTemplate.update(sql, email, password);
    }




}
