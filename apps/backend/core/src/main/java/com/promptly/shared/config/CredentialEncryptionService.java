package com.promptly.shared.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * AES-256-GCM encryption service for tenant API keys stored in MongoDB (SaaS mode).
 * <p>
 * The encryption key is sourced from the environment variable
 * {@code PROMPTLY_LLM_CREDENTIAL_ENCRYPTION_KEY} and must be exactly 32 bytes (base64-encoded).
 * <p>
 * In self-hosted mode, this service is not used — API keys come only from env vars.
 */
@Slf4j
@Service
public class CredentialEncryptionService {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 128;
    private static final int IV_LENGTH = 12;
    private static final String PREFIX = "enc:aes256:";

    private final SecretKeySpec secretKey;
    private final SecureRandom secureRandom = new SecureRandom();

    public CredentialEncryptionService(PromptlyProperties properties) {
        String keyBase64 = properties.getLlm().getCredentialEncryptionKey();
        if (keyBase64 != null && !keyBase64.isBlank()) {
            byte[] keyBytes = Base64.getDecoder().decode(keyBase64);
            if (keyBytes.length != 32) {
                throw new IllegalArgumentException(
                        "Credential encryption key must be 32 bytes (256-bit). Got " + keyBytes.length + " bytes.");
            }
            this.secretKey = new SecretKeySpec(keyBytes, "AES");
            log.info("Credential encryption initialized (AES-256-GCM)");
        } else {
            this.secretKey = null;
            log.info("Credential encryption not configured — tenant API keys cannot be stored encrypted. " +
                     "Set promptly.llm.credential-encryption-key to enable.");
        }
    }

    /**
     * Encrypts a plaintext credential (e.g., API key).
     *
     * @param plaintext the credential to encrypt
     * @return an encrypted string prefixed with {@code enc:aes256:}
     * @throws IllegalStateException if encryption key is not configured
     */
    public String encrypt(String plaintext) {
        if (secretKey == null) {
            throw new IllegalStateException(
                    "Cannot encrypt credentials — no encryption key configured. " +
                    "Set PROMPTLY_LLM_CREDENTIAL_ENCRYPTION_KEY environment variable.");
        }
        try {
            byte[] iv = new byte[IV_LENGTH];
            secureRandom.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(GCM_TAG_LENGTH, iv));
            byte[] encrypted = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            // Combine IV + ciphertext
            byte[] combined = new byte[IV_LENGTH + encrypted.length];
            System.arraycopy(iv, 0, combined, 0, IV_LENGTH);
            System.arraycopy(encrypted, 0, combined, IV_LENGTH, encrypted.length);

            return PREFIX + Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to encrypt credential", e);
        }
    }

    /**
     * Decrypts a credential previously encrypted by this service.
     *
     * @param ciphertext the encrypted string (must start with {@code enc:aes256:})
     * @return the decrypted plaintext
     */
    public String decrypt(String ciphertext) {
        if (secretKey == null) {
            throw new IllegalStateException("Cannot decrypt — no encryption key configured.");
        }
        if (!ciphertext.startsWith(PREFIX)) {
            throw new IllegalArgumentException("Invalid encrypted credential format — expected prefix: " + PREFIX);
        }
        try {
            byte[] combined = Base64.getDecoder().decode(ciphertext.substring(PREFIX.length()));
            byte[] iv = new byte[IV_LENGTH];
            byte[] encrypted = new byte[combined.length - IV_LENGTH];
            System.arraycopy(combined, 0, iv, 0, IV_LENGTH);
            System.arraycopy(combined, IV_LENGTH, encrypted, 0, encrypted.length);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(GCM_TAG_LENGTH, iv));
            byte[] decrypted = cipher.doFinal(encrypted);

            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to decrypt credential", e);
        }
    }

    /**
     * Checks whether a value is an encrypted credential.
     */
    public boolean isEncrypted(String value) {
        return value != null && value.startsWith(PREFIX);
    }

    /**
     * Checks whether encryption is configured and available.
     */
    public boolean isConfigured() {
        return secretKey != null;
    }

    /**
     * Returns a masked hint of the plaintext (e.g., "sk-ant-...••••") for display in the UI.
     */
    public String mask(String plaintext) {
        if (plaintext == null || plaintext.length() < 8) {
            return "••••••••";
        }
        return plaintext.substring(0, Math.min(6, plaintext.length())) + "...••••";
    }
}
