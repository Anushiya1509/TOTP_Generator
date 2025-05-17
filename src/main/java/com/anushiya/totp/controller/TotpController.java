package com.anushiya.totp.controller;

import com.anushiya.totp.service.TotpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/totp")
public class TotpController {

    private static final Logger logger = LoggerFactory.getLogger(TotpController.class);

    @Autowired
    private TotpService totpService;

    @PostMapping("/generate")
    public ResponseEntity<Map<String, Object>> generateTotpSecret(@RequestParam String username) {
        Map<String, Object> response = new HashMap<>();
        logger.info("Generating TOTP for user: {}", username);
        try {
            String secret = totpService.generateTotp(username);
            response.put("secret", secret);
            response.put("message", "TOTP secret generated successfully");
            logger.debug("TOTP secret for user [{}]: {}", username, secret);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to generate TOTP for user [{}]: {}", username, e.getMessage(), e);
            response.put("error", "Failed to generate TOTP secret");
            response.put("details", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyTotp(@RequestParam String username, @RequestParam String code) {
        Map<String, Object> response = new HashMap<>();
        logger.info("Verifying TOTP for user: {}", username);
        try {
            boolean isValid = totpService.verifyTotp(username, code);
            response.put("valid", isValid);
            response.put("message", isValid ? "TOTP verification successful" : "TOTP verification failed");
            logger.debug("Verification result for user [{}]: {}", username, isValid);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to verify TOTP for user [{}]: {}", username, e.getMessage(), e);
            response.put("error", "Failed to verify TOTP code");
            response.put("details", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
