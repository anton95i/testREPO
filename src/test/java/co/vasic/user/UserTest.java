package co.vasic.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class UserTest {

    User user;

    @BeforeEach
    void beforeEach() {
        user = User.builder()
                .username("username")
                .password("5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8") // password
                .build();
    }

    @Test
    @DisplayName("Authorize with invalid credentials")
    void testAuthorize__invalidCredentials() {
        boolean result = user.authorize("wrong password");
        assertFalse(result);
    }

    @Test
    @DisplayName("Authorize with valid credentials")
    void testAuthorize__validCredentials() {
        boolean result = user.authorize("password");
        assertTrue(result);
    }

}
