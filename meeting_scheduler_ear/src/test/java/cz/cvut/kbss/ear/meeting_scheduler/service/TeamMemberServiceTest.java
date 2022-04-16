package cz.cvut.kbss.ear.meeting_scheduler.service;

import cz.cvut.kbss.ear.meeting_scheduler.environment.Generator;
import cz.cvut.kbss.ear.meeting_scheduler.exception.NotEnoughPermissionException;
import cz.cvut.kbss.ear.meeting_scheduler.model.*;
import cz.cvut.kbss.ear.meeting_scheduler.model.enums.TeamRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

import static cz.cvut.kbss.ear.meeting_scheduler.model.enums.TeamRolePermission.CREATE_FEEDBACK;
import static org.junit.jupiter.api.Assertions.*;



@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
public class TeamMemberServiceTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private TeamMemberService sut;

    private TeamMember teamMember;

    @BeforeEach
    public void setup() {
        this.teamMember = Generator.generateMember();
        em.persist(teamMember);
    }

    @Test
    public void createFeedbackTest() {
        Team team = new Team();
        team.setId(1);
        teamMember.setTeam(team);

        Feedback feedback = new Feedback();

        MeetingOption meetingOption = new MeetingOption();
        ArrayList<MeetingOption> options = new ArrayList<>();
        options.add(meetingOption);

        MeetingOffer meetingOffer = new MeetingOffer();
        meetingOffer.setOptions(options);
        meetingOffer.setTeam(teamMember.getTeam());

        em.persist(feedback);
        em.persist(meetingOption);
        em.persist(meetingOffer);

        sut.createFeedback(teamMember, feedback, meetingOption, meetingOffer);

        assertTrue(teamMember.getTeamRole().hasPermision(CREATE_FEEDBACK));
        assertEquals(feedback, meetingOffer.getOptions().get(0).getFeedBackList().get(0));
        assertNotNull(em.find(Feedback.class, feedback.getId()));
    }

    @Test
    public void leaveTeamTest() {
        UserAccount user = new UserAccount("test", "test", "test");
        sut.createTeam(user, "testteam");

        TeamMember teamMember = user.getTeamMemberships().get(0);
        Team team = teamMember.getTeam();

        final int teamSize = team.getMembers().size();
        em.persist(user);
        em.persist(team);
        em.persist(teamMember);

        sut.leaveTeam(teamMember);

        final TeamMember result = em.find(TeamMember.class, teamMember.getId());

        assertNull(result);
        assertFalse(team.getMembers().contains(teamMember));
        assertEquals(teamSize - 1, team.getMembers().size());
    }

    @Test
    public void createMeetingOfferTest() {
        Team team = new Team();
        teamMember.setTeam(team);
        teamMember.setTeamRole(TeamRole.MAINTAINER);
        List<MeetingOption> options = new ArrayList<>();
        options.add(new MeetingOption());

        sut.createMeetingOffer(teamMember, options);

        MeetingOffer resultOffer = em.find(MeetingOffer.class, teamMember.getTeam().getMeetingOffers().get(0).getId());

        assertNotNull(resultOffer);
        assertEquals(1, teamMember.getTeam().getMeetingOffers().size());
    }

    @Test
    public void createMeetingOfferTestNotEnoughPermission() {
        teamMember.setTeamRole(TeamRole.MEMBER);
        List<MeetingOption> options = new ArrayList<>();
        options.add(new MeetingOption());

        assertThrows(NotEnoughPermissionException.class, () -> sut.createMeetingOffer(teamMember, options));
    }

    @Test
    public void createMeetingFromOfferTest() {
        Team team = new Team();
        team.setId(1);
        teamMember.setTeam(team);
        teamMember.setTeamRole(TeamRole.MAINTAINER);
        List<MeetingOption> options = new ArrayList<>();
        options.add(new MeetingOption());
        sut.createMeetingOffer(teamMember, options);

        sut.createMeetingFromOffer(teamMember, teamMember.getTeam().getMeetingOffers().get(0), teamMember.getTeam().getMeetingOffers().get(0).getOptions().get(0));

        Meeting resultMeeting = em.find(Meeting.class, teamMember.getTeam().getMeetings().get(0).getId());

        assertNotNull(resultMeeting);
        assertEquals(1, team.getMeetings().size());
        assertEquals(new ArrayList<>(), team.getMeetingOffers());
    }

    @Test
    public void createMeetingFromOfferNotEnoughPermissionTest() {
        Team team = new Team();
        teamMember.setTeam(team);
        teamMember.setTeamRole(TeamRole.MAINTAINER);
        List<MeetingOption> options = new ArrayList<>();
        options.add(new MeetingOption());
        sut.createMeetingOffer(teamMember, options);

        teamMember.setTeamRole(TeamRole.MEMBER);

        assertThrows(NotEnoughPermissionException.class, () -> sut.createMeetingFromOffer(teamMember, teamMember.getTeam().getMeetingOffers().get(0), teamMember.getTeam().getMeetingOffers().get(0).getOptions().get(0)));
    }

    @Test
    public void createMeetingFromOfferTeamOfMemberAndOfferDontMatchTest() {
        Team team = new Team();
        team.setId(1);
        teamMember.setTeam(team);
        teamMember.setTeamRole(TeamRole.MAINTAINER);
        List<MeetingOption> options = new ArrayList<>();
        options.add(new MeetingOption());
        sut.createMeetingOffer(teamMember, options);

        Team team2 = new Team();
        team2.setId(2);
        teamMember.getTeam().getMeetingOffers().get(0).setTeam(team2);

        assertThrows(NotEnoughPermissionException.class, () -> sut.createMeetingFromOffer(teamMember, teamMember.getTeam().getMeetingOffers().get(0), teamMember.getTeam().getMeetingOffers().get(0).getOptions().get(0)));
    }

    @Test
    public void cancelMeetingTest() {
        Team team = new Team();
        team.setId(1);
        teamMember.setTeam(team);
        teamMember.setTeamRole(TeamRole.MAINTAINER);
        Meeting meeting = new Meeting();
        meeting.setTeam(team);
        team.addMeeting(meeting);

        assertEquals(1, team.getMeetings().size());

        em.persist(meeting);

        sut.cancelMeeting(teamMember, meeting);

        assertNull(em.find(Meeting.class, meeting.getId()));
        assertEquals(0, team.getMeetings().size());
    }

    @Test
    public void cancelMeetingNotEnoughPermissionTest() {
        Team team = new Team();
        teamMember.setTeam(team);
        teamMember.setTeamRole(TeamRole.MAINTAINER);
        Meeting meeting = new Meeting();
        meeting.setTeam(team);
        team.addMeeting(meeting);

        teamMember.setTeamRole(TeamRole.MEMBER);

        assertThrows(NotEnoughPermissionException.class, () -> sut.cancelMeeting(teamMember, meeting));
    }

    @Test
    public void cancelMeetingTeamOfMemberAndOfferDontMatchTest() {
        Team team = new Team();
        team.setId(1);
        teamMember.setTeam(team);
        teamMember.setTeamRole(TeamRole.MAINTAINER);
        Meeting meeting = new Meeting();
        meeting.setTeam(team);
        team.addMeeting(meeting);

        Team team2 = new Team();
        team2.setId(0);
        teamMember.getTeam().getMeetings().get(0).setTeam(team2);

        assertThrows(NotEnoughPermissionException.class, () -> sut.cancelMeeting(teamMember, meeting));
    }

    @Test
    public void cancelMeetingOfferTest() {
        Team team = new Team();
        team.setId(1);
        teamMember.setTeam(team);
        teamMember.setTeamRole(TeamRole.MAINTAINER);
        List<MeetingOption> options = new ArrayList<>();
        options.add(new MeetingOption());
        sut.createMeetingOffer(teamMember, options);

        MeetingOffer offer = teamMember.getTeam().getMeetingOffers().get(0);

        sut.cancelMeetingOffer(teamMember, offer);

        assertNull(em.find(MeetingOffer.class, offer.getId()));
        assertEquals(new ArrayList<>(), teamMember.getTeam().getMeetingOffers());
    }

    @Test
    public void cancelMeetingOfferNotEnoughPermissionTest() {
        Team team = new Team();
        teamMember.setTeam(team);
        teamMember.setTeamRole(TeamRole.MAINTAINER);
        List<MeetingOption> options = new ArrayList<>();
        options.add(new MeetingOption());
        sut.createMeetingOffer(teamMember, options);

        teamMember.setTeamRole(TeamRole.MEMBER);
        MeetingOffer offer = teamMember.getTeam().getMeetingOffers().get(0);

        assertThrows(NotEnoughPermissionException.class, () -> sut.cancelMeetingOffer(teamMember, offer));
    }

    @Test
    public void cancelMeetingOfferTeamOfMemberAndOfferDontMatchTest() {
        Team team = new Team();
        team.setId(1);
        teamMember.setTeam(team);
        teamMember.setTeamRole(TeamRole.MAINTAINER);
        List<MeetingOption> options = new ArrayList<>();
        options.add(new MeetingOption());
        sut.createMeetingOffer(teamMember, options);
        MeetingOffer offer = teamMember.getTeam().getMeetingOffers().get(0);

        Team team2 = new Team();
        team2.setId(2);
        teamMember.getTeam().getMeetingOffers().get(0).setTeam(team2);

        assertThrows(NotEnoughPermissionException.class, () -> sut.cancelMeetingOffer(teamMember, offer));
    }

    @Test
    public void addTeamMemberTest() {
        Team team = new Team();
        UserAccount user = Generator.generateUser();
        teamMember.setTeamRole(TeamRole.MAINTAINER);
        teamMember.setTeam(team);

        sut.addTeamMember(teamMember, user, TeamRole.MEMBER);

        TeamMember newMember = user.getTeamMemberships().get(0);
        newMember.setId(1);
        em.persist(team);
        em.persist(user);
        em.persist(newMember);

        assertNotNull(newMember);
        assertEquals(1, user.getTeamMemberships().size());
        assertEquals(1, team.getMembers().size());
        assertEquals(TeamRole.MEMBER, newMember.getTeamRole());
    }

    @Test
    public void addTeamMemberTestNotEnoughPermission() {
        UserAccount user = Generator.generateUser();
        teamMember.setTeamRole(TeamRole.MEMBER);

        assertThrows(NotEnoughPermissionException.class, () -> sut.addTeamMember(teamMember, user, TeamRole.MEMBER));
    }

    @Test
    public void kickTeamMemberTest() {
        Team team = new Team();
        UserAccount user = Generator.generateUser();
        teamMember.setTeamRole(TeamRole.MAINTAINER);
        teamMember.setTeam(team);
        sut.addTeamMember(teamMember, user, TeamRole.MEMBER);

        TeamMember newMember = user.getTeamMemberships().get(0);
        newMember.setId(1);
        em.persist(team);
        em.persist(user);
        em.persist(newMember);

        assertNotNull(newMember);
        assertEquals(1, user.getTeamMemberships().size());
        assertEquals(1, team.getMembers().size());

        sut.kickTeamMember(teamMember, newMember);

        assertNull(em.find(TeamMember.class, newMember.getId()));
        assertEquals(0, user.getTeamMemberships().size());
        assertEquals(0, team.getMembers().size());
    }

    @Test
    public void kickTeamMemberNotEnoughPermissionTest() {
        teamMember.setTeamRole(TeamRole.MEMBER);
        TeamMember newMember = new TeamMember();

        assertThrows(NotEnoughPermissionException.class, () -> sut.kickTeamMember(teamMember, newMember));
    }

    @Test
    public void changeTeamRoleTest() {
        Team team = new Team();
        teamMember.setTeamRole(TeamRole.OWNER);
        teamMember.setTeam(team);
        TeamMember newMember = Generator.generateMember();
        newMember.setTeamRole(TeamRole.MEMBER);
        newMember.setTeam(team);

        sut.changeTeamRole(teamMember, newMember, TeamRole.MAINTAINER);

        em.persist(newMember);

        assertEquals(TeamRole.MAINTAINER, em.find(TeamMember.class, newMember.getId()).getTeamRole());
    }

    @Test
    public void changeTeamRoleNotEnoughPermissionTest() {
        Team team = new Team();
        teamMember.setTeamRole(TeamRole.MAINTAINER);
        teamMember.setTeam(team);
        TeamMember newMember = Generator.generateMember();
        newMember.setTeamRole(TeamRole.MEMBER);
        newMember.setTeam(team);

        assertThrows(NotEnoughPermissionException.class, () -> sut.changeTeamRole(teamMember, newMember, TeamRole.MAINTAINER));
    }

    @Test
    public void changeTeamRoleTeamsDontMatchTest() {
        Team team = new Team();
        teamMember.setTeamRole(TeamRole.OWNER);
        teamMember.setTeam(team);
        TeamMember newMember = Generator.generateMember();
        newMember.setTeamRole(TeamRole.MEMBER);
        newMember.setTeam(new Team());

        assertThrows(NotEnoughPermissionException.class, () -> sut.changeTeamRole(teamMember, newMember, TeamRole.MAINTAINER));
    }


    @Test
    public void kickMaintainerTest() {
        Team team = new Team();
        teamMember.setTeamRole(TeamRole.OWNER);
        teamMember.setTeam(team);
        UserAccount user = Generator.generateUser();

        sut.addTeamMember(teamMember, user, TeamRole.MAINTAINER);

        TeamMember newMember = user.getTeamMemberships().get(0);
        newMember.setId(1);
        em.persist(team);
        em.persist(user);
        em.persist(newMember);

        sut.kickMaintainer(teamMember, newMember);

        assertNull(em.find(TeamMember.class, newMember.getId()));
        assertEquals(0, user.getTeamMemberships().size());
        assertEquals(0, team.getMembers().size());
    }

    @Test
    public void kickMaintainerNotEnoughPermissionTest() {
        Team team = new Team();
        teamMember.setTeamRole(TeamRole.OWNER);
        teamMember.setTeam(team);
        UserAccount user = Generator.generateUser();

        sut.addTeamMember(teamMember, user, TeamRole.MAINTAINER);
        TeamMember newMember = user.getTeamMemberships().get(0);
        teamMember.setTeamRole(TeamRole.MEMBER);

        assertThrows(NotEnoughPermissionException.class, () -> sut.kickMaintainer(teamMember, newMember));
    }

    @Test
    public void kickMaintainerTeamsDontMatch() {
        Team team = new Team();
        teamMember.setTeamRole(TeamRole.OWNER);
        teamMember.setTeam(team);
        TeamMember newMember = Generator.generateMember();
        newMember.setTeam(new Team());

        assertThrows(NotEnoughPermissionException.class, () -> sut.kickMaintainer(teamMember, newMember));
    }

    @Test
    public void createTeamTest() {
        String teamName = "testTeam";
        UserAccount user = Generator.generateUser();

        sut.createTeam(user, teamName);
        TeamMember member = user.getTeamMemberships().get(0);
        Team team = member.getTeam();
        team.setId(1);
        em.persist(team);
        em.persist(user);
        em.persist(member);

        assertNotNull(team);
        assertNotNull(em.find(Team.class, team.getId()));
        assertEquals(team.getMembers().get(0).getTeamRole(), TeamRole.OWNER);
        assertEquals(team.getMembers().get(0).getUserAccount(), user);
        assertEquals(1, user.getTeamMemberships().size());
        assertEquals(1, team.getMembers().size());
    }


    @Test
    public void dissolveTeamTest() {
        String teamName = "testTeam";
        UserAccount user = Generator.generateUser();

        sut.createTeam(user, teamName);
        TeamMember member = user.getTeamMemberships().get(0);
        Team team = member.getTeam();
        team.setId(1);

        em.persist(team);
        em.persist(user);
        em.persist(member);

        assertNotNull(em.find(Team.class, team.getId()));

        sut.dissolveTeam(member);

        assertNull(em.find(Team.class, team.getId()));
    }

    @Test
    public void dissolveTeamNotEnoughPermissionTest() {
        TeamMember member = new TeamMember();
        member.setTeamRole(TeamRole.MAINTAINER);

        assertThrows(NotEnoughPermissionException.class, () -> sut.dissolveTeam(member));
    }
}
