package com.wilzwert.myjobs.infrastructure.api.rest.controller;


import com.wilzwert.myjobs.core.domain.model.user.command.CreatePasswordCommand;
import com.wilzwert.myjobs.core.domain.model.user.ports.driving.CreateNewPasswordUseCase;
import com.wilzwert.myjobs.core.domain.model.user.ports.driving.ResetPasswordUseCase;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.*;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author Wilhelm Zwertvaegher
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
    public void resetPassword(@RequestBody @Valid ResetPasswordRequest resetPasswordRequest) {
        resetPasswordUseCase.resetPassword(resetPasswordRequest.getEmail());
    }

    @PostMapping("/password")
    public void newPassword(@RequestBody @Valid NewPasswordRequest newPasswordRequest) {
        createNewPasswordUseCase.createNewPassword(new CreatePasswordCommand(newPasswordRequest.getPassword(), newPasswordRequest.getToken()));
    }
}