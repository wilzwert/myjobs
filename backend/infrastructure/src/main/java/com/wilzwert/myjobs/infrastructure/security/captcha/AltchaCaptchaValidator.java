package com.wilzwert.myjobs.infrastructure.security.captcha;

import lombok.extern.slf4j.Slf4j;
import org.altcha.altcha.v2.Altcha;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "captcha.provider", havingValue = "altcha")
@Slf4j
public class AltchaCaptchaValidator implements CaptchaValidator {

    private final String altchaSecretKey;

    private final boolean altchaAlwaysValid;

    public AltchaCaptchaValidator(
            @Value("${altcha.secret-key}") String altchaSecretKey,
            @Value("${captcha.always-valid}") boolean altchaAlwaysValid
    ) {
        this.altchaSecretKey = altchaSecretKey;
        this.altchaAlwaysValid = altchaAlwaysValid;
    }

    public Altcha.Challenge createChallenge() throws Exception {
        var options = new Altcha.CreateChallengeOptions()
                .algorithm("PBKDF2/SHA-256")
                .cost(5_000)          // PBKDF2 iterations
                .hmacSignatureSecret("your-secret-key")
                .expiresInSeconds(600); // 10 minutes

        return Altcha.createChallenge(options);
    }

    public boolean validateCaptcha(String captchaResponse) {
        if(altchaAlwaysValid) {
            log.info("Altcha Always Valid");
            return true;
        }

        log.info("Validating altcha");

        // From a base64-encoded payload submitted by the client:
        Altcha.VerifySolutionResult result;
        try {
            result = Altcha.verifySolution(
                    captchaResponse,
                    altchaSecretKey,
                    Altcha.kdf("PBKDF2/SHA-256"));
        } catch (Exception e) {
            log.error("Unable to verify altcha solution {}", e.getMessage());
            throw new RuntimeException(e);
        }

        return result.verified();
    }
}