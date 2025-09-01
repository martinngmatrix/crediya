package co.com.bancolombia.model.user.constants.messages;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class UserErrorMessagesTest {

    @Test
    void shouldHaveCorrectEmailAlreadyExistsMessage() {
        assertEquals("Correo ya registrado", UserErrorMessages.EMAIL_ALREADY_EXISTS);
    }

    @Test
    void shouldHaveCorrectCredentialsMessage() {
        assertEquals("Credenciales inválidas", UserErrorMessages.INVALID_CREDENTIALS);
    }

    @Test
    void constantShouldNotBeNullOrEmpty() {
        assertNotNull(UserErrorMessages.EMAIL_ALREADY_EXISTS);
        assertNotNull(UserErrorMessages.INVALID_CREDENTIALS);
        assertFalse(UserErrorMessages.EMAIL_ALREADY_EXISTS.isEmpty());
        assertFalse(UserErrorMessages.INVALID_CREDENTIALS.isEmpty());
    }
}