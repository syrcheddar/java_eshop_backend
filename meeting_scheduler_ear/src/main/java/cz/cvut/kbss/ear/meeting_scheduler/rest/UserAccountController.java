package cz.cvut.kbss.ear.meeting_scheduler.rest;

import cz.cvut.kbss.ear.meeting_scheduler.model.UserAccount;
import cz.cvut.kbss.ear.meeting_scheduler.rest.util.RestUtils;
import cz.cvut.kbss.ear.meeting_scheduler.security.model.AuthenticationToken;
import cz.cvut.kbss.ear.meeting_scheduler.service.UserAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class UserAccountController {

    private static final Logger LOG = LoggerFactory.getLogger(UserAccountController.class);

    private final UserAccountService userAccountService;

    @Autowired
    public UserAccountController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    /**
     * Registers a new user.
     *
     * @param userAccount User data
     */
    @PostMapping(value = "/rest/user_account", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> register(@RequestBody UserAccount userAccount) {
        userAccountService.createAccount(userAccount);
        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/current");
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @PreAuthorize("@MySecurityEvaluator.isNotNull(#principal)")
    @GetMapping(value = "/current", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getCurrent(Principal principal) {
        final AuthenticationToken auth = (AuthenticationToken) principal;
        UserAccount userAccount = auth.getPrincipal().getUser();
        return userAccount.getUsername() + "\n" + userAccount.getEmail() + "\n" + "Number of memberships: " + userAccount.getTeamMemberships().size();
    }
}
