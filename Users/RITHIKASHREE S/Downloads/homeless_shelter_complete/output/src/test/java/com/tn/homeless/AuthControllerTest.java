package com.tn.homeless;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tn.homeless.dto.AuthRequest;
import com.tn.homeless.dto.RegisterRequest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for /auth endpoints.
 * Uses an in-memory H2 database (configured via test properties).
 *
 * TEST CASES:
 *  TC-01  Register a new user  → 200 + success:true
 *  TC-02  Register duplicate   → 409 Conflict
 *  TC-03  Login valid creds    → 200 + token present
 *  TC-04  Login wrong password → 401 Unauthorized
 *  TC-05  Login blank username → 400 Validation error
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.mail.host=smtp.mailtrap.io"   // dummy — no real mail in tests
})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    /* ── TC-01: Successful Registration ────────────────────────── */
    @Test @Order(1)
    @DisplayName("TC-01: Register new user → 200 success")
    void testRegisterSuccess() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("testvolunteer");
        req.setPassword("test1234");
        req.setEmail("testvolunteer@example.com");
        req.setRole("VOLUNTEER");

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.username").value("testvolunteer"));
    }

    /* ── TC-02: Duplicate Username ──────────────────────────────── */
    @Test @Order(2)
    @DisplayName("TC-02: Register duplicate username → 409 conflict")
    void testRegisterDuplicate() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("testvolunteer");   // same as TC-01
        req.setPassword("another123");
        req.setEmail("other@example.com");
        req.setRole("VOLUNTEER");

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.success").value(false));
    }

    /* ── TC-03: Successful Login ────────────────────────────────── */
    @Test @Order(3)
    @DisplayName("TC-03: Login with correct credentials → 200 + token")
    void testLoginSuccess() throws Exception {
        AuthRequest req = new AuthRequest("testvolunteer", "test1234");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.token").isNotEmpty())
            .andExpect(jsonPath("$.data.role").value("VOLUNTEER"));
    }

    /* ── TC-04: Wrong Password ──────────────────────────────────── */
    @Test @Order(4)
    @DisplayName("TC-04: Login with wrong password → 401")
    void testLoginWrongPassword() throws Exception {
        AuthRequest req = new AuthRequest("testvolunteer", "wrongpassword");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.success").value(false));
    }

    /* ── TC-05: Blank Username Validation ───────────────────────── */
    @Test @Order(5)
    @DisplayName("TC-05: Login with blank username → 400 validation error")
    void testLoginBlankUsername() throws Exception {
        AuthRequest req = new AuthRequest("", "password");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false));
    }
}
