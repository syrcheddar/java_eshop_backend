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
        @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = TestConfiguration.class)})
public class UserAccountDaoTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private UserAccountDao sut;

    @Test
    public void findByUsernameReturnsCorrectUser() {
        String username = "TestUsername";
        UserAccount user = new UserAccount(username, "TestUserPassword", "testemail@test.test");
        em.persist(user);

        UserAccount resultUser = sut.findByUsername(username);

        assertNotNull(resultUser);
        assertEquals(user.getId(), user.getId());
    }

    @Test
    public void findByUsernameReturnsNullWhenNothingMatch() {
        UserAccount user = new UserAccount("TestUsername", "TestUserPassword", "testemail@test.test");
        em.persist(user);

        UserAccount resultUser = sut.findByUsername("Test");

        assertNull(resultUser);
    }

    @Test
    public void findByEmailReturnsCorrectUser() {
        String email = "testemail@test.test";
        UserAccount user = new UserAccount("TestUsername", "TestUserPassword", email);
        em.persist(user);

        UserAccount resultUser = sut.findByEmail(email);

        assertNotNull(resultUser);
        assertEquals(user.getId(), user.getId());
    }

    @Test
    public void findByEmailReturnsNullWhenNothingMatch() {
        UserAccount user = new UserAccount("TestUsername", "TestUserPassword", "testemail@test.test");
        em.persist(user);

        UserAccount resultUser = sut.findByUsername("Test@test.test");

        assertNull(resultUser);
    }

    @Test
    public void viewMyTeamsReturnsCorrectTeams() {
        UserAccount userAccount1 = new UserAccount("User1", "password", "test@test.test");
        UserAccount userAccount2 = new UserAccount("User2", "password2", "test2@test.test");
        String teamName = "testTeam";
        String teamName2 = "testTeam2";
        Team team = new Team(teamName);
        Team team2 = new Team(teamName2);
        Team team3 = new Team("testTeam3");

        TeamMember teamMember1 = new TeamMember(userAccount1, team, TeamRole.MEMBER);
        team.addMember(teamMember1);
        TeamMember teamMember2 = new TeamMember(userAccount2, team, TeamRole.MEMBER);
        team.addMember(teamMember2);
        TeamMember teamMember3 = new TeamMember(userAccount1, team2, TeamRole.MEMBER);
        team2.addMember(teamMember3);
        TeamMember teamMember4 = new TeamMember(userAccount2, team3, TeamRole.MEMBER);
        team3.addMember(teamMember4);

        List<String> expectedList = new ArrayList<>();
        expectedList.add(teamName);
        expectedList.add(teamName2);

        em.persist(userAccount1);
        em.persist(userAccount2);
        em.persist(team);
        em.persist(team2);
        em.persist(team3);
        em.persist(teamMember1);
        em.persist(teamMember2);
        em.persist(teamMember3);
        em.persist(teamMember4);

        List<String> resultList = sut.viewMyTeams(userAccount1.getUsername());

        assertEquals(expectedList.size(), resultList.size());
        assertEquals(expectedList, resultList);
    }

    @Test
    public void viewMyTeamsReturnsEmptyListUserHasNoTeams() {
        String username = "User1";
        UserAccount user = new UserAccount(username, "password", "test@test.test");

        List<String> expectedList = new ArrayList<>();
        em.persist(user);

        List<String> resultList = sut.viewMyTeams(username);

        assertEquals(0, resultList.size());
        assertEquals(expectedList, resultList);
    }

    @Test
    public void viewMyTeamsReturnsEmptyListWhenUserDoesNotExist() {
        List<String> expectedList = new ArrayList<>();

        List<String> resultList = sut.viewMyTeams("username");

        assertEquals(0, resultList.size());
        assertEquals(expectedList, resultList);
    }
}
