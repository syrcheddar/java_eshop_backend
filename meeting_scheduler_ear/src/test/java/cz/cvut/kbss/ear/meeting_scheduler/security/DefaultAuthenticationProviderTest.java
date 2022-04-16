package cz.cvut.kbss.ear.meeting_scheduler.security;

import cz.cvut.kbss.ear.meeting_scheduler.environment.Generator;
import cz.cvut.kbss.ear.meeting_scheduler.model.UserAccount;
import cz.cvut.kbss.ear.meeting_scheduler.security.model.UserAccountDetails;
import cz.cvut.kbss.ear.meeting_scheduler.service.UserAccountService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

// SpringBootTest starts the whole Spring application context in test mode
@SpringBootTest
// Transactional on class -> each test is run in a transaction which is rolled back after test finishes
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
public class DefaultAuthenticationProviderTest {

    private final UserAccount userAccount = Generator.generateUser();
    private final String rawPassword = userAccount.getPassword();
    @Autowired
    private UserAccountService userAccountService;
    @Autowired
    private DefaultAuthenticationProvider provider;

    @BeforeEach
    public void setUp() {
        userAccountService.createAccount(userAccount);
        SecurityContextHolder.setContext(new SecurityContextImpl());
    }

    @AfterEach
    public void tearDown() {
        SecurityContextHolder.setContext(new SecurityContextImpl());
    }

    @Test
    public void successfulAuthenticationSetsSecurityContext() {
        final Authentication auth = new UsernamePasswordAuthenticationToken(userAccount.getUsername(), rawPassword);
        final SecurityContext context = SecurityContextHolder.getContext();
        assertNull(context.getAuthentication());
        final Authentication result = provider.authenticate(auth);
        assertNotNull(result);
        assertTrue(result.isAuthenticated());
        assertNotNull(SecurityContextHolder.getContext());
        final UserAccountDetails details = (UserAccountDetails) SecurityContextHolder.getContext().getAuthentication().getDetails();
        assertEquals(userAccount.getUsername(), details.getUsername());
        assertTrue(result.isAuthenticated());
    }

    @Test
    public void authenticateThrowsUserNotFoundExceptionForUnknownUsername() {
        final Authentication auth = new UsernamePasswordAuthenticationToken("unknownUsername", rawPassword);
        try {
            assertThrows(UsernameNotFoundException.class, () -> provider.authenticate(auth));
        } finally {
            final SecurityContext context = SecurityContextHolder.getContext();
            assertNull(context.getAuthentication());
        }
    }

    @Test
    public void authenticateThrowsBadCredentialsForInvalidPassword() {
        final Authentication auth = new UsernamePasswordAuthenticationToken(userAccount.getUsername(), "unknownPassword");
        try {
            assertThrows(BadCredentialsException.class, () -> provider.authenticate(auth));
        } finally {
            final SecurityContext context = SecurityContextHolder.getContext();
            assertNull(context.getAuthentication());
        }
    }

    @Test
    public void supportsUsernameAndPasswordAuthentication() {
        assertTrue(provider.supports(UsernamePasswordAuthenticationToken.class));
    }
}
