package cz.cvut.kbss.ear.meeting_scheduler.dao;

import cz.cvut.kbss.ear.meeting_scheduler.MeetingSchedulerApplication;
import cz.cvut.kbss.ear.meeting_scheduler.environment.TestConfiguration;
import cz.cvut.kbss.ear.meeting_scheduler.model.Team;
import cz.cvut.kbss.ear.meeting_scheduler.model.TeamMember;
import cz.cvut.kbss.ear.meeting_scheduler.model.UserAccount;
import cz.cvut.kbss.ear.meeting_scheduler.model.enums.TeamRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ComponentScan(basePackageClasses = MeetingSchedulerApplication.class, excludeFilters = {
        //@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SystemInitializer.class),
        @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = TestConfiguration.class)})
public class TeamDaoTest {
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private TeamDao sut;

    @Test
    public void findByNameReturnsCorrectTeam() {
        String teamName = "TestTeamName";
        Team team = new Team(teamName);
        em.persist(team);

        Team resultTeam = sut.findByName(teamName);

        assertNotNull(resultTeam);
        assertEquals(resultTeam.getId(), team.getId());
    }

    @Test
    public void findByNameReturnsNullWhenNothingMatch() {
        Team team = new Team("TestTeamName");
        em.persist(team);

        Team resultTeam = sut.findByName("Test");

        assertNull(resultTeam);
    }

    @Test
    public void getTeamOwnerTestReturnsCorrectOwner() {
        UserAccount userAccount1 = new UserAccount("Team Owner", "owner", "owner@test.test");
        UserAccount userAccount2 = new UserAccount("Team Member", "member", "member@test.test");
        Team team = new Team("testTeam");
        TeamMember teamMember1 = new TeamMember(userAccount1, team, TeamRole.OWNER);
        team.addMember(teamMember1);
        TeamMember teamMember2 = new TeamMember(userAccount2, team, TeamRole.MEMBER);
        team.addMember(teamMember2);

        em.persist(userAccount1);
        em.persist(userAccount2);
        em.persist(team);
        em.persist(teamMember1);
        em.persist(teamMember2);

        TeamMember resultOwner = sut.getTeamOwner(team.getName());

        assertNotNull(resultOwner);
        assertEquals(teamMember1.getId(), resultOwner.getId());
    }

    @Test
    public void getTeamOwnerTestReturnsNullWhenTeamHasNoOwner() {
        Team team = new Team("testTeam");
        em.persist(team);

        TeamMember resultOwner = sut.getTeamOwner(team.getName());

        assertNull(resultOwner);
    }

    @Test
    public void getTeamOwnerTestReturnsNullWhenTeamDoesNotExist() {
        TeamMember resultOwner = sut.getTeamOwner("testTeam");

        assertNull(resultOwner);
    }

    @Test
    public void viewTeamMembersReturnsCorrectMembers() {
        UserAccount userAccount1 = new UserAccount("Team Owner", "owner", "owner@test.test");
        UserAccount userAccount2 = new UserAccount("Team Member", "member", "member@test.test");
        String teamName = "testTeam";
        Team team = new Team(teamName);
        TeamMember teamMember1 = new TeamMember(userAccount1, team, TeamRole.MEMBER);
        team.addMember(teamMember1);
        TeamMember teamMember2 = new TeamMember(userAccount2, team, TeamRole.MEMBER);
        team.addMember(teamMember2);

        List<String> expectedList = new ArrayList<>();
        expectedList.add(userAccount1.getUsername());
        expectedList.add(userAccount2.getUsername());

        em.persist(userAccount1);
        em.persist(userAccount2);
        em.persist(team);
        em.persist(teamMember1);
        em.persist(teamMember2);
        List<String> resultList = sut.viewTeamMembers(teamName);

        assertEquals(expectedList.size(), resultList.size());
        assertEquals(expectedList, resultList);
    }

    @Test
    public void viewTeamMembersReturnsEmptyListWhenTeamIsEmpty() {
        String teamName = "testTeam";
        Team team = new Team(teamName);
        List<String> expectedList = new ArrayList<>();
        em.persist(team);

        List<String> resultList = sut.viewTeamMembers(teamName);

        assertEquals(0, resultList.size());
        assertEquals(expectedList, resultList);
    }

    @Test
    public void viewTeamMembersReturnsEmptyListWhenTeamDoesNotExist() {
        List<String> expectedList = new ArrayList<>();

        List<String> resultList = sut.viewTeamMembers("testTeam");

        assertEquals(0, resultList.size());
        assertEquals(expectedList, resultList);
    }
}