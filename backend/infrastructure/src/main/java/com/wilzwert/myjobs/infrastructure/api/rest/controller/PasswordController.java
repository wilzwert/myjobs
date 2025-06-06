package com.wilzwert.myjobs.infrastructure.api.rest.controller;


import com.wilzwert.myjobs.core.domain.model.user.command.ChangePasswordCommand;
import com.wilzwert.myjobs.core.domain.model.user.command.CreatePasswordCommand;
import com.wilzwert.myjobs.core.domain.model.user.ports.driving.ChangePasswordUseCase;
import com.wilzwert.myjobs.core.domain.model.user.ports.driving.CreateNewPasswordUseCase;
import com.wilzwert.myjobs.core.domain.model.user.ports.driving.ResetPasswordUseCase;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.*;
import com.wilzwert.myjobs.infrastructure.security.service.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
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

    private final ChangePasswordUseCase changePasswordUseCase;

    public PasswordController(ResetPasswordUseCase resetPasswordUseCase, CreateNewPasswordUseCase createNewPasswordUseCase, ChangePasswordUseCase changePasswordUseCase) {
        this.resetPasswordUseCase = resetPasswordUseCase;
        this.createNewPasswordUseCase = createNewPasswordUseCase;
        this.changePasswordUseCase = changePasswordUseCase;
    }

    @PostMapping("/password/reset")
    public void resetPassword(@RequestBody @Valid ResetPasswordRequest resetPasswordRequest) {
        resetPasswordUseCase.resetPassword(resetPasswordRequest.getEmail());
    }

    @PostMapping("/password")
    public void newPassword(@RequestBody @Valid NewPasswordRequest newPasswordRequest) {
        createNewPasswordUseCase.createNewPassword(new CreatePasswordCommand(newPasswordRequest.getPassword(), newPasswordRequest.getToken()));
    }

    /**
     * Changes the current user's password
     * @param changePasswordRequest the request for password change
     * @param authentication the current authentication
     */
    @PutMapping("/me/password")
    @ResponseStatus(HttpStatus.OK)
    public void changePassword(@RequestBody @Valid ChangePasswordRequest changePasswordRequest, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        changePasswordUseCase.changePassword(new ChangePasswordCommand(changePasswordRequest.getPassword(), changePasswordRequest.getOldPassword(), userDetails.getId()));
    }
}