package cz.cvut.kbss.ear.meeting_scheduler.rest;

import cz.cvut.kbss.ear.meeting_scheduler.model.Feedback;
import cz.cvut.kbss.ear.meeting_scheduler.model.MeetingOffer;
import cz.cvut.kbss.ear.meeting_scheduler.model.MeetingOption;
import cz.cvut.kbss.ear.meeting_scheduler.model.TeamMember;
import cz.cvut.kbss.ear.meeting_scheduler.model.enums.FeedbackRating;
import cz.cvut.kbss.ear.meeting_scheduler.security.model.AuthenticationToken;
import cz.cvut.kbss.ear.meeting_scheduler.service.MeetingOfferService;
import cz.cvut.kbss.ear.meeting_scheduler.service.TeamMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@PreAuthorize("@MySecurityEvaluator.isNotNull(#principal)")
@RequestMapping("/rest/team")
public class TeamMemberController {

    private final TeamMemberService teamMemberService;

    private final MeetingOfferService meetingOfferService;

    @Autowired
    public TeamMemberController(TeamMemberService teamMemberService, MeetingOfferService meetingOfferService) {
        this.teamMemberService = teamMemberService;
        this.meetingOfferService = meetingOfferService;
    }

    @PreAuthorize("hasPermission(authentication.principal,#teamName,'MEMBER') OR hasPermission(authentication.principal,#teamName,'MAINTAINER')")
    @GetMapping(value = "/leave_team", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void leaveTeam(Principal principal, @RequestBody String teamName) {
        final AuthenticationToken auth = (AuthenticationToken) principal;
        TeamMember tm = getTeamMember(auth, teamName);
        teamMemberService.leaveTeam(tm);

    }

    @PostMapping(value = "/feedback", produces = MediaType.APPLICATION_JSON_VALUE)
    public void createFeedback(Principal principal, String teamName, int meetingOfferID, String comment, String rating, int meetingOptionID) {
        final AuthenticationToken auth = (AuthenticationToken) principal;
        Feedback feedback = new Feedback(FeedbackRating.valueOf(rating), comment);

        MeetingOption meetingOption = meetingOfferService.findOptionByID(meetingOptionID);
        MeetingOffer meetingOffer = meetingOfferService.findOfferByID(meetingOfferID);

        TeamMember tm = getTeamMember(auth, teamName);
        teamMemberService.createFeedback(tm, feedback, meetingOption, meetingOffer);
    }

    @GetMapping(value = "/team_members", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> viewTeamMembers(Principal principal, @RequestBody String teamName) {
        System.out.println(principal + "\n");
        final AuthenticationToken auth = (AuthenticationToken) principal;
        TeamMember tm = getTeamMember(auth, teamName);
        return new ResponseEntity<>(teamMemberService.viewTeamMembers(tm), HttpStatus.OK);

    }

    private TeamMember getTeamMember(AuthenticationToken auth, String teamName) {
        TeamMember result = null;
        for (TeamMember tm : auth.getPrincipal().getUser().getTeamMemberships()) {
            if (tm.getTeam().getName().equals(teamName)) result = tm;
        }
        return result;
    }
}
