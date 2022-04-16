package cz.cvut.kbss.ear.meeting_scheduler.rest;

import cz.cvut.kbss.ear.meeting_scheduler.model.*;
import cz.cvut.kbss.ear.meeting_scheduler.rest.util.RestUtils;
import cz.cvut.kbss.ear.meeting_scheduler.security.model.AuthenticationToken;
import cz.cvut.kbss.ear.meeting_scheduler.security.model.UserAccountDetails;
import cz.cvut.kbss.ear.meeting_scheduler.service.MeetingOfferService;
import cz.cvut.kbss.ear.meeting_scheduler.service.MeetingService;
import cz.cvut.kbss.ear.meeting_scheduler.service.TeamMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;

@RestController
@PreAuthorize("@MySecurityEvaluator.isNotNull(#principal)")
@RequestMapping("/rest/meeting")
public class MeetingController {

    private final MeetingOfferService meetingOfferService;
    private final TeamMemberService teamMemberService;
    private final MeetingService meetingService;

    @Autowired
    public MeetingController(TeamMemberService teamMemberService, MeetingOfferService meetingOfferService, MeetingService meetingService) {
        this.meetingOfferService = meetingOfferService;
        this.teamMemberService = teamMemberService;
        this.meetingService = meetingService;
    }


    @PreAuthorize("@MySecurityEvaluator.hasPermission(authentication.principal,#teamName,'OWNER') OR @MySecurityEvaluator.hasPermission(authentication.principal,#teamName,'MAINTAINER')")
    @PostMapping(value = "/meeting_offer", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createMeetingOffer(Principal principal, String teamName, String dateS, String timeFrom, String timeTo, String location, String description) {
        final AuthenticationToken auth = (AuthenticationToken) principal;
        TeamMember tm = getTeamMember(auth, teamName);
        Team team = findTeam(auth.getPrincipal(), teamName);
        MeetingOption meetingOption = new MeetingOption(team, Date.valueOf(dateS), Time.valueOf(timeFrom), Time.valueOf(timeTo), location, description);
        ArrayList<MeetingOption> meetingOptions = new ArrayList<>();
        meetingOptions.add(meetingOption);
        teamMemberService.createMeetingOffer(tm, meetingOptions);
        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/offer_{id}", meetingOptions.get(0).getId());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @PreAuthorize("@MySecurityEvaluator.hasPermission(authentication.principal,#teamName,'OWNER') OR @MySecurityEvaluator.hasPermission(authentication.principal,#teamName,'MAINTAINER')")
    @PostMapping(value = "/meeting", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createMeeting(Principal principal, String teamName, int meetingOfferID, int meetingOptionID) {
        final AuthenticationToken auth = (AuthenticationToken) principal;

        MeetingOption meetingOption = meetingOfferService.findOptionByID(meetingOptionID);
        MeetingOffer meetingOffer = meetingOfferService.findOfferByID(meetingOfferID);

        TeamMember tm = getTeamMember(auth, teamName);
        teamMemberService.createMeetingFromOffer(tm, meetingOffer, meetingOption);

        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/{id}", meetingOption.getId());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @PreAuthorize("@MySecurityEvaluator.hasPermission(authentication.principal,#teamName,'OWNER') OR @MySecurityEvaluator.hasPermission(authentication.principal,#teamName,'MAINTAINER')")
    @DeleteMapping(value = "/cancel_meeting", produces = MediaType.APPLICATION_JSON_VALUE)
    public void cancelMeeting(Principal principal, String teamName, int meetingID) {
        final AuthenticationToken auth = (AuthenticationToken) principal;
        TeamMember tm = getTeamMember(auth, teamName);
        Meeting meeting = meetingService.findMeetingByID(meetingID);
        teamMemberService.cancelMeeting(tm, meeting);

    }

    @PreAuthorize("@MySecurityEvaluator.hasPermission(authentication.principal,#teamName,'OWNER') OR @MySecurityEvaluator.hasPermission(authentication.principal,#teamName,'MAINTAINER')")
    @DeleteMapping(value = "/cancel_meeting_offer", produces = MediaType.APPLICATION_JSON_VALUE)
    public void cancelMeetingOffer(Principal principal, String teamName, int meetingOfferId) {
        final AuthenticationToken auth = (AuthenticationToken) principal;
        TeamMember tm = getTeamMember(auth, teamName);
        MeetingOffer meetingOffer = meetingOfferService.findOfferByID(meetingOfferId);
        teamMemberService.cancelMeetingOffer(tm, meetingOffer);
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
