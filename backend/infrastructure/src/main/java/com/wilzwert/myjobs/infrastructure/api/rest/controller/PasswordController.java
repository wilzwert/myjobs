package com.wilzwert.myjobs.infrastructure.api.rest.controller;


import com.wilzwert.myjobs.core.domain.command.CreatePasswordCommand;
import com.wilzwert.myjobs.core.domain.ports.driving.*;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.*;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:11:43
 * TODO : add rate limiting on public endpoints
 */
@RestController
@Slf4j
@RequestMapping("/api/user")
public class PasswordController {

    private final ResetPasswordUseCase resetPasswordUseCase;

    private final CreateNewPasswordUseCase createNewPasswordUseCase;

    public PasswordController(ResetPasswordUseCase resetPasswordUseCase, CreateNewPasswordUseCase createNewPasswordUseCase) {
        this.resetPasswordUseCase = resetPasswordUseCase;
        this.createNewPasswordUseCase = createNewPasswordUseCase;
    }

    @PostMapping("/password/reset")
    public ResponseEntity<?> resetPassword(@RequestBody @Valid ResetPasswordRequest resetPasswordRequest) {
        resetPasswordUseCase.resetPassword(resetPasswordRequest.getEmail());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/password")
    public ResponseEntity<?> newPassword(@RequestBody @Valid NewPasswordRequest newPasswordRequest) {
        createNewPasswordUseCase.createNewPassword(new CreatePasswordCommand(newPasswordRequest.getPassword(), newPasswordRequest.getToken()));
        return ResponseEntity.ok().build();
    }
}