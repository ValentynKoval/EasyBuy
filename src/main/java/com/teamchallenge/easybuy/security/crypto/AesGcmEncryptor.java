package com.teamchallenge.easybuy.security.crypto;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Objects;

/**
 * A utility class for encrypting and decrypting data using AES-256 GCM.
 * This class uses a passphrase stored in an environment variable for key derivation.
 * The output is a Base64-encoded string in a specific format: "version$salt$iv$ciphertext".
 *
 * This approach is suitable for encrypting sensitive data like personal information,
 * ensuring it is securely stored and transmitted.
 */
public final class AesGcmEncryptor {
    private static final String VERSION = "v1";
    private static final int SALT_LEN = 16;          // solt for PBKDF2
    private static final int IV_LEN = 12;            // nonce/IV for GCM
    private static final int KEY_LEN_BITS = 256;
    private static final int GCM_TAG_LEN_BITS = 128;
    private static final int PBKDF2_ITERATIONS = 210_000;
    private static final SecureRandom RNG = new SecureRandom();

    private AesGcmEncryptor() {}

    /**
     * Encrypts a plaintext string using AES-256 GCM.
     * The method derives a key from an environment variable passphrase and generates
     * a random salt and initialization vector (IV) for each operation.
     *
     * @param plaintext The string to be encrypted.
     * @return The encrypted string in a Base64-encoded format: "v1$salt$iv$ciphertext".
     * Returns null if the input is null.
     * @throws IllegalStateException if encryption fails or the passphrase is not set.
     */
    public static String encrypt(String plaintext) {
        if (plaintext == null) return null;
        byte[] salt = randomBytes(SALT_LEN);
        SecretKey key = deriveKey(loadPassphrase(), salt);
        byte[] iv = randomBytes(IV_LEN);
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LEN_BITS, iv));
            byte[] ct = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            return String.join("$", VERSION, b64(salt), b64(iv), b64(ct));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Encryption failed", e);
        }
    }

    /**
     * Decrypts an encrypted payload string.
     * The payload is expected to be in the "v1$salt$iv$ciphertext" format.
     * The method uses the same key derivation process as encryption to decrypt the data.
     *
     * @param payload The Base64-encoded encrypted string.
     * @return The original plaintext string.
     * @throws IllegalArgumentException if the payload format is invalid or the version is unsupported.
     * @throws IllegalStateException if decryption fails or the passphrase is not set.
     */
    public static String decrypt(String payload) {
        if (payload == null) return null;
        String[] parts = payload.split("\\$");
        if (parts.length != 4) throw new IllegalArgumentException("Invalid encrypted payload format");
        String version = parts[0];
        if (!Objects.equals(version, VERSION)) {
            throw new IllegalArgumentException("Unsupported version: " + version);
        }
        byte[] salt = b64d(parts[1]);
        byte[] iv = b64d(parts[2]);
        byte[] ct = b64d(parts[3]);
        SecretKey key = deriveKey(loadPassphrase(), salt);
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LEN_BITS, iv));
            return new String(cipher.doFinal(ct), StandardCharsets.UTF_8);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Decryption failed", e);
        }
    }

    private static SecretKey deriveKey(String passphrase, byte[] salt) {
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(passphrase.toCharArray(), salt, PBKDF2_ITERATIONS, KEY_LEN_BITS);
            byte[] keyBytes = skf.generateSecret(spec).getEncoded();
            return new SecretKeySpec(keyBytes, "AES");
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Key derivation failed", e);
        }
    }

    private static String loadPassphrase() {
        String pass = System.getenv("APP_CRYPTO_PASSWORD");
        if (pass == null || pass.isBlank()) {
            throw new IllegalStateException("Environment variable APP_CRYPTO_PASSWORD is not set");
        }
        return pass;
    }

    private static byte[] randomBytes(int len) {
        byte[] out = new byte[len];
        RNG.nextBytes(out);
        return out;
    }

    private static String b64(byte[] b) { return Base64.getEncoder().encodeToString(b); }
    private static byte[] b64d(String s) { return Base64.getDecoder().decode(s); }
}