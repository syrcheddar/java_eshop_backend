package cz.cvut.kbss.ear.meeting_scheduler.service;

import cz.cvut.kbss.ear.meeting_scheduler.model.Team;
import cz.cvut.kbss.ear.meeting_scheduler.model.TeamMember;
import cz.cvut.kbss.ear.meeting_scheduler.model.UserAccount;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
public class UserAccountServiceTest {

    @Autowired
    private UserAccountService sut;

    @PersistenceContext
    private EntityManager em;

    private UserAccount user;

    @BeforeEach
    public void setup() {
        user = new UserAccount("test", "test123", "test@email.com");
        em.persist(user);
    }

    @Test
    public void viewProfileInfoTest() {
        List<String> expected = new ArrayList<>();
        expected.add(user.getUsername());
        expected.add(user.getEmail());

        List<String> result = sut.viewProfileInfo(user);

        assertEquals(expected.size(), result.size());
        assertEquals(expected, result);
    }

    @Test
    public void viewProfileInfoReturnsArrayWithNullStringsWhenEmptyUser() {
        UserAccount userAccount = new UserAccount();
        ArrayList<String> expected = new ArrayList<>();
        expected.add(null);
        expected.add(null);

        List<String> result = sut.viewProfileInfo(userAccount);

        assertEquals(expected.size(), result.size());
        assertEquals(expected, result);
    }

    @Test
    public void viewMyTeamsTest() {
        Team team = new Team("testTeam");
        Team team2 = new Team("testTeam2");
        TeamMember teamMember = new TeamMember(user, team, TeamRole.OWNER);
        TeamMember teamMember2 = new TeamMember(user, team2, TeamRole.MEMBER);

        team.addMember(teamMember);
        team.addMember(teamMember2);
        List<String> expectedList = new ArrayList<>();
        expectedList.add("testTeam");
        expectedList.add("testTeam2");

        em.persist(team);
        em.persist(team2);
        em.persist(teamMember);
        em.persist(teamMember2);

        List<String> resultList = sut.viewMyTeams(user);

        assertEquals(expectedList.size(), resultList.size());
        assertEquals(expectedList, resultList);
    }

    @Test
    public void viewMyTeamsIsEmptyTest() {
        List<String> expected = new ArrayList<>();
        List<String> result = sut.viewMyTeams(user);
        assertEquals(expected, result);
    }
}
