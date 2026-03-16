package com.incture.e_commerce.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;

import com.incture.e_commerce.entity.User;
import com.incture.e_commerce.model.JwtAuthenticationToken;
import com.incture.e_commerce.utils.JwtUtil;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationProviderTest {

	@Mock
    private static JwtUtil jwtUtil;
    @Mock
    private static UserService userService;
    @InjectMocks
    private static JwtAuthenticationProvider provider;
    
    @Test
    void authenticate_WhenReceiveValidToken_ShouldReturnUserSuccessfully() {

        String token = "valid-token";
        String username = "test@mail.com";

        JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(token);

        User user = new User();
        user.setEmail(username);
        user.setRole("ROLE_CUSTOMER");

        when(jwtUtil.validateAndExtractUsername(token))
                .thenReturn(username);

        when(userService.loadUserByUsername(username))
                .thenReturn(user);

        Authentication result = provider.authenticate(authenticationToken);

        assertNotNull((Object)result);
        assertEquals(user, (User)result.getPrincipal());
    }

    @Test
    void authenticate_WhenReceiveInvalidToken_ShouldThrowBadCredentialsException() {

        String token = "invalid-token";

        JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(token);

        when(jwtUtil.validateAndExtractUsername(token))
                .thenReturn(null);

        assertThrows(BadCredentialsException.class,
                () -> provider.authenticate(authenticationToken));
    }

    @Test
    void supports_WhenReceiveCorrectTokenClass_ShouldSupportSuccessfully() {
        assertTrue(provider.supports(JwtAuthenticationToken.class));
    }

    @Test
    void supports_WhenReceivedWrongTokenClass_ShouldNotSupport() {
        assertFalse(provider.supports(Authentication.class));
    }
    
}
