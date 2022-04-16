package cz.cvut.kbss.ear.meeting_scheduler.rest;

import cz.cvut.kbss.ear.meeting_scheduler.exception.TeamMemberException;
import cz.cvut.kbss.ear.meeting_scheduler.model.Team;
import cz.cvut.kbss.ear.meeting_scheduler.model.TeamMember;
import cz.cvut.kbss.ear.meeting_scheduler.model.UserAccount;
import cz.cvut.kbss.ear.meeting_scheduler.model.enums.TeamRole;
import cz.cvut.kbss.ear.meeting_scheduler.rest.util.RestUtils;
import cz.cvut.kbss.ear.meeting_scheduler.security.model.AuthenticationToken;
import cz.cvut.kbss.ear.meeting_scheduler.security.model.UserAccountDetails;
import cz.cvut.kbss.ear.meeting_scheduler.service.TeamMemberService;
import cz.cvut.kbss.ear.meeting_scheduler.service.TeamService;
import cz.cvut.kbss.ear.meeting_scheduler.service.UserAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@PreAuthorize("@MySecurityEvaluator.isNotNull(#principal)")
@RestController
@RequestMapping("/rest/team")
public class TeamController {

    private static final Logger LOG = LoggerFactory.getLogger(UserAccountController.class);

    private final TeamMemberService teamMemberService;

    private final TeamService teamService;

    private final UserAccountService userAccountService;

    @Autowired
    public TeamController(TeamMemberService teamMemberService, TeamService teamService, UserAccountService userAccountService) {
        this.teamMemberService = teamMemberService;
        this.teamService = teamService;
        this.userAccountService = userAccountService;
    }

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createTeam(Principal principal, @RequestBody String teamName) {
        final AuthenticationToken auth = (AuthenticationToken) principal;
        teamService.createTeam(auth.getPrincipal().getUser(), teamName);
        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/teamtest");
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @PreAuthorize("@MySecurityEvaluator.hasPermission(authentication.principal,#teamName,'OWNER') OR @MySecurityEvaluator.hasPermission(authentication.principal,#teamName,'MAINTAINER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping(value = "/kick_member", produces = MediaType.APPLICATION_JSON_VALUE)
    public void kickMember(Principal principal, String teamName, String userToKick) {
        kickUser((AuthenticationToken) principal, teamName, userToKick);
    }


    @PreAuthorize("@MySecurityEvaluator.hasPermission(authentication.principal,#teamName,'OWNER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping(value = "/kick_maintainer", produces = MediaType.APPLICATION_JSON_VALUE)
    public void kickMaintainer(Principal principal, String teamName, String userToKick) {
        kickUser((AuthenticationToken) principal, teamName, userToKick);
    }


    @PreAuthorize("@MySecurityEvaluator.hasPermission(authentication.principal,#teamName,'OWNER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping(value = "/change_role", produces = MediaType.APPLICATION_JSON_VALUE)
    public void changeTeamRole(Principal principal, String teamName, String teamRole, String username) {
        final AuthenticationToken auth = (AuthenticationToken) principal;
        TeamMember member = null;
        for (TeamMember currMember : findTeam(auth.getPrincipal(), teamName).getMembers()) {
            if (currMember.getUserAccount().getUsername().equals(username)) {
                member = currMember;
            }
        }
        if (member == null) throw new TeamMemberException("No such member");
        teamMemberService.changeTeamRole(getTeamMember(auth, teamName), member, TeamRole.valueOf(teamRole));
    }

    @PreAuthorize("@MySecurityEvaluator.hasPermission(authentication.principal,#teamName,'OWNER')")
    @PostMapping(value = "/add_member", produces = MediaType.APPLICATION_JSON_VALUE)
    public void addTeamMember(Principal principal, String teamName, String teamRole, String username) {

        final AuthenticationToken auth = (AuthenticationToken) principal;

        TeamMember teamMemberAdding = getTeamMember(auth, teamName);
        UserAccount userToAdd = userAccountService.findUserByUsername(username);

        teamMemberService.addTeamMember(teamMemberAdding, userToAdd, TeamRole.valueOf(teamRole));
    }


    @PreAuthorize("@MySecurityEvaluator.hasPermission(authentication.principal,#team,'OWNER')")
    @DeleteMapping(value = "/dissolve", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void dissolveTeam(Principal principal, @RequestBody String team) {
        final AuthenticationToken auth = (AuthenticationToken) principal;
        for (TeamMember tm : auth.getPrincipal().getUser().getTeamMemberships()) {
            if (tm.getTeam().getName().equals(team)) {
                teamMemberService.dissolveTeam(tm);
                break;
            }
        }
    }

    private void kickUser(AuthenticationToken principal, String teamName, String userToKick) {
        TeamMember teamMemberToKick = null;
        Team team = findTeam(principal.getPrincipal(), teamName);
        for (TeamMember tm : team.getMembers())
            if (tm.getUserAccount().getUsername().equals(userToKick)) teamMemberToKick = tm;
        teamMemberService.kickMaintainer(getTeamMember(principal, teamName), teamMemberToKick);
    }

    private Team findTeam(UserAccountDetails userAccountDetails, String teamName) {
        Team result = null;
        for (TeamMember member : userAccountDetails.getUser().getTeamMemberships()) {
            if (member.getTeam().getName().equals(teamName)) result = member.getTeam();
        }
        return result;
    }

    private TeamMember getTeamMember(AuthenticationToken auth, String teamName) {
        TeamMember result = null;
        for (TeamMember tm : auth.getPrincipal().getUser().getTeamMemberships()) {
            if (tm.getTeam().getName().equals(teamName)) result = tm;
        }
        return result;
    }
}
