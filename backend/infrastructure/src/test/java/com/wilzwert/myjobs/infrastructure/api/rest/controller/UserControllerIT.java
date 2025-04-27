package com.wilzwert.myjobs.infrastructure.api.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.model.user.UserId;
import com.wilzwert.myjobs.core.domain.ports.driven.PasswordHasher;
import com.wilzwert.myjobs.core.domain.ports.driven.UserService;
import com.wilzwert.myjobs.core.domain.shared.validation.ErrorCode;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.ChangePasswordRequest;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.UpdateUserRequest;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.UserResponse;
import com.wilzwert.myjobs.infrastructure.configuration.AbstractBaseIntegrationTest;
import com.wilzwert.myjobs.infrastructure.security.service.UserDetailsImpl;
import io.jsonwebtoken.lang.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class UserControllerIT extends AbstractBaseIntegrationTest  {
    private final static String USER_URL = "/api/user";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    Authentication authentication;

    @BeforeEach
    public void setup() {
        UserDetailsImpl fakeUser = new UserDetailsImpl(
                new UserId(UUID.fromString("abcd4321-4321-4321-4321-123456789012")),
                "otherexisting@example.com",
                "otherexisting",
                "USER",
                "password",
                Collections.of(new SimpleGrantedAuthority("USER"))
        );
        authentication = new UsernamePasswordAuthenticationToken(fakeUser, null, fakeUser.getAuthorities());
    }

    @Nested
    class USerControllerGetIt {
        @Test
        public void whenUnauthenticated_thenShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(post(USER_URL))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser
        public void shouldGetUser() throws Exception {
            MvcResult mvcResult = mockMvc.perform(get(USER_URL).with(authentication(authentication)))
                    .andExpect(status().isOk())
                    .andReturn();
            UserResponse userResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserResponse.class);
            assertThat(userResponse).isNotNull();
            assertEquals("otherexisting@example.com", userResponse.getEmail());
            assertEquals("otherexistinguser", userResponse.getUsername());
            assertEquals("OtherExisting", userResponse.getFirstName());
            assertEquals("OtherUser", userResponse.getLastName());
            assertEquals("2025-03-29T09:46:09.475Z", userResponse.getCreatedAt());
            assertEquals("PENDING", userResponse.getEmailStatus());
        }
    }

    @Nested
    class USerControllerDeleteIt {
        @Test
        public void whenUnauthenticated_thenShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(delete(USER_URL))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser
        public void shouldDeleteUser() throws Exception {
            // Simule ton user
            UserDetailsImpl fakeUser = new UserDetailsImpl(
                    new UserId(UUID.fromString("abcd9876-9876-9876-9876-123456789012")),
                    "usertodelete@example.com",
                    "usertodelete",
                    "USER",
                    "password",
                    Collections.of(new SimpleGrantedAuthority("USER"))
            );
            Authentication auth = new UsernamePasswordAuthenticationToken(fakeUser, null, fakeUser.getAuthorities());

            mockMvc.perform(delete(USER_URL).with(authentication(auth)))
                    .andExpect(status().isNoContent());

            // since we have UserService at our disposal, we can check that the deleted user cannot be retrieved
            assertThat(userService.findById(new UserId(UUID.fromString("abcd9876-9876-9876-9876-123456789012"))).isEmpty());
        }
    }

    @Nested
    class UserControllerUpdateIT {
        private final static String UPDATE_URL = USER_URL;

        private UpdateUserRequest updateUserRequest;

        @BeforeEach
        public void setup() {
            // setup a default valid signup request
            updateUserRequest = new UpdateUserRequest();
            updateUserRequest.setEmail("otherexisting-updatedt@example.com");
            updateUserRequest.setUsername("otherexistinguserupdated");
            updateUserRequest.setFirstName("OtherExistingUpdated");
            updateUserRequest.setLastName("OtherUserUpdated");
        }

        @Test
        public void whenUnauthenticated_thenShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(patch(UPDATE_URL))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        public void whenRequestBodyEmpty_thenShouldReturnBadRequest() throws Exception {
            mockMvc.perform(patch(UPDATE_URL).with(authentication(authentication)))
                    .andExpect(status().isBadRequest());
        }
        @Test
        public void whenEmailEmpty_thenShouldReturnBadRequestWhenEmailEmpty() throws Exception {
                updateUserRequest.setEmail("");
            mockMvc.perform(patch(UPDATE_URL).with(authentication(authentication)).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateUserRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void whenEmailInvalid_thenShouldReturnBadRequest() throws Exception {
            updateUserRequest.setEmail("test");
            mockMvc.perform(patch(UPDATE_URL).with(authentication(authentication)).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateUserRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errors.email").value(ErrorCode.INVALID_EMAIL.name()));
        }

        @Test
        public void whenFirstNameEmpty_thenShouldReturnBadRequest() throws Exception {
            updateUserRequest.setFirstName("");
            mockMvc.perform(patch(UPDATE_URL).with(authentication(authentication)).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateUserRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errors.firstName").value(ErrorCode.FIELD_CANNOT_BE_EMPTY.name()));
        }

        @Test
        public void whenLastNameEmpty_thenShouldReturnBadRequest() throws Exception {
            updateUserRequest.setLastName("");
            mockMvc.perform(patch(UPDATE_URL).with(authentication(authentication)).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateUserRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errors.lastName").value(ErrorCode.FIELD_CANNOT_BE_EMPTY.name()));
        }

        @Test
        public void whenUsernameTooShort_thenShouldReturnBadRequest() throws Exception {
            updateUserRequest.setUsername("T");
            mockMvc.perform(patch(UPDATE_URL).with(authentication(authentication)).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateUserRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errors.username").value(ErrorCode.FIELD_TOO_SHORT.name()));
        }

        @Test
        public void whenUsernameTooLong_thenShouldReturnBadRequest() throws Exception {
            updateUserRequest.setUsername("thisisafartoolongusernamethatshouldtriggeravalidationerror");
            mockMvc.perform(patch(UPDATE_URL).with(authentication(authentication)).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateUserRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errors.username").value(ErrorCode.FIELD_TOO_LONG.name()));
        }

        @Test
        public void whenEmailAlreadyExists_thenShouldReturnBadRequest() throws Exception {
            // we know we already have a User with the email 'existing@example.com' (see resources/test-data/user.json
            updateUserRequest.setEmail("existing@example.com");

            mockMvc.perform(patch(UPDATE_URL).with(authentication(authentication)).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateUserRequest)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("message").value(ErrorCode.USER_ALREADY_EXISTS.name()));
        }

        @Test
        public void whenUsernameAlreadyExists_thenShouldReturnBadRequest() throws Exception {
            // we know we already have a User with the username 'existinguser' (see resources/test-data/user.json
            updateUserRequest.setUsername("existinguser");

            mockMvc.perform(patch(UPDATE_URL).with(authentication(authentication)).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateUserRequest)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("message").value(ErrorCode.USER_ALREADY_EXISTS.name()));
        }

        @Test
        public void shouldUpdateUser() throws Exception {
            MvcResult mvcResult = mockMvc.perform(patch(UPDATE_URL).with(authentication(authentication)).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateUserRequest)))
                    .andExpect(status().isOk())
                    .andReturn();

            UserResponse userResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserResponse.class);
            assertThat(userResponse).isNotNull();
            assertEquals("otherexisting-updatedt@example.com", userResponse.getEmail());
            assertEquals("otherexistinguserupdated", userResponse.getUsername());
            assertEquals("OtherExistingUpdated", userResponse.getFirstName());
            assertEquals("OtherUserUpdated", userResponse.getLastName());
            assertEquals("2025-03-29T09:46:09.475Z", userResponse.getCreatedAt());
            assertEquals("PENDING", userResponse.getEmailStatus());


            // "rollback" update to allow predictable further tests
            Optional<User> foundUser = userService.findByEmail("otherexisting-updatedt@example.com");
            if(foundUser.isEmpty()) {
                fail("Created user should be retrievable.");
            }
            else {
                User user = foundUser.get();
                userService.save(user.update("otherexisting@example.com", "otherexistinguser", "OtherExisting", "OtherUser"));
            }
        }
    }

    @Nested
    class UserControllerChangePasswordIT {

        private static final String CHANGE_PASSWORD_URL = USER_URL + "/me/password";

        private ChangePasswordRequest changePasswordRequest;

        private Authentication authentication;

        @Autowired
        private PasswordHasher passwordHasher;

        @BeforeEach
        public void setUp()  {
            // this is a valid password request
            // it should be changed per case for testing
            changePasswordRequest = new ChangePasswordRequest();
            changePasswordRequest.setPassword("Dcba4321!");
            changePasswordRequest.setOldPassword("Abcd1234!");

            UserDetailsImpl fakeUser = new UserDetailsImpl(
                    new UserId(UUID.fromString("abcd6543-6543-6543-6543-123456789012")),
                    "changepassword@example.com",
                    "changepassword",
                    "USER",
                    "password",
                    Collections.of(new SimpleGrantedAuthority("USER"))
            );
            authentication = new UsernamePasswordAuthenticationToken(fakeUser, null, fakeUser.getAuthorities());
        }

        @Test
        public void whenNoAuth_thenShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(put(CHANGE_PASSWORD_URL))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        public void whenRequestBodyEmpty_thenShouldReturnBadRequest() throws Exception {
            mockMvc.perform(put(CHANGE_PASSWORD_URL).with(authentication(authentication)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void whenOldPasswordEmpty_thenShouldReturnBadRequest() throws Exception {
            changePasswordRequest.setOldPassword("");
            mockMvc.perform(put(CHANGE_PASSWORD_URL).with(authentication(authentication)).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(changePasswordRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("message").value("Validation error"))
                    .andExpect(jsonPath("errors.oldPassword").value(ErrorCode.FIELD_CANNOT_BE_EMPTY.name()));
        }

        @Test
        public void whenNewPasswordEmpty_thenShouldReturnBadRequest() throws Exception {
            changePasswordRequest.setPassword("");
            mockMvc.perform(put(CHANGE_PASSWORD_URL).with(authentication(authentication)).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(changePasswordRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("message").value("Validation error"))
                    .andExpect(jsonPath("errors.password").value(ErrorCode.FIELD_CANNOT_BE_EMPTY.name()));
        }

        @Test
        public void whenOldPasswordDoesntMatch_thenShouldReturnBadRequest() throws Exception {
            changePasswordRequest.setOldPassword("Pqrs4321!");
            mockMvc.perform(put(CHANGE_PASSWORD_URL).with(authentication(authentication)).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(changePasswordRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("message").value(ErrorCode.USER_PASSWORD_MATCH_FAILED.name()));
        }

        @Test
        public void whenNewPasswordWeak_thenShouldReturnBadRequest() throws Exception {
            changePasswordRequest.setPassword("abcd1234!");
            mockMvc.perform(put(CHANGE_PASSWORD_URL).with(authentication(authentication)).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(changePasswordRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("message").value("Validation error"))
                    .andExpect(jsonPath("errors.password").value(ErrorCode.USER_WEAK_PASSWORD.name()));
        }

        @Test
        public void ShouldUpdatePassword() throws Exception {
            mockMvc.perform(put(CHANGE_PASSWORD_URL).with(authentication(authentication)).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(changePasswordRequest)))
                    .andExpect(status().isOk());


            // check that the password actually changed
            // then "rollback" update to allow predictable further tests
            Optional<User> foundUser = userService.findByEmail("changepassword@example.com");
            if(foundUser.isEmpty()) {
                fail("User should be retrievable.");
            }
            else {
                User user = foundUser.get();
                assertTrue(passwordHasher.verifyPassword("Dcba4321!", user.getPassword()));

                userService.save(user.updatePassword("Abcd1234!", passwordHasher.hashPassword("Abcd1234!")));
            }
        }

    }

}
