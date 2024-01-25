package com.ticketty.tickettyapp.repository.user;

import org.springframework.dao.DataAccessException;
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

    public boolean saveUser(String email, String password) {
        String sql = "INSERT INTO user(email, password) VALUES(?, ?)";
        int affectedRows = jdbcTemplate.update(sql, email, password);

        // 반환된 행 수를 기반으로 저장 성공 여부를 판단
        return affectedRows > 0;
    }

}
