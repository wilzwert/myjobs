package com.wilzwert.myjobs.infrastructure.api.rest.controller;


import com.wilzwert.myjobs.core.domain.model.user.command.ValidateEmailCommand;
import com.wilzwert.myjobs.core.domain.model.user.ports.driving.SendVerificationEmailUseCase;
import com.wilzwert.myjobs.core.domain.model.user.ports.driving.ValidateEmailUseCase;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.ValidateEmailRequest;
import com.wilzwert.myjobs.infrastructure.security.service.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * @author Wilhelm Zwertvaegher
 * Date:03/06/2025
 * Time:12:01
 */
@RestController
@Slf4j
@RequestMapping("/api/user/me")
public class EmailController {

    private final ValidateEmailUseCase validateEmailUseCase;

    private final SendVerificationEmailUseCase sendVerificationEmailUseCase;

    public EmailController(ValidateEmailUseCase validateEmailUseCase, SendVerificationEmailUseCase sendVerificationEmailUseCase) {
        this.validateEmailUseCase = validateEmailUseCase;
        this.sendVerificationEmailUseCase = sendVerificationEmailUseCase;
    }

    @PostMapping("/email/validation")
    @ResponseStatus(HttpStatus.OK)
    public void validateEmail(@RequestBody @Valid ValidateEmailRequest validateEmailRequest) {
        validateEmailUseCase.validateEmail(new ValidateEmailCommand(validateEmailRequest.getCode()));
    }

    @PostMapping("/email/verification")
    @ResponseStatus(HttpStatus.OK)
    public void sendVerificationEmail(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        sendVerificationEmailUseCase.sendVerificationEmail(userDetails.getId());
    }
}
