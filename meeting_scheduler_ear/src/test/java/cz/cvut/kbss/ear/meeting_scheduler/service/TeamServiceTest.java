package cz.cvut.kbss.ear.meeting_scheduler.service;

import cz.cvut.kbss.ear.meeting_scheduler.environment.Generator;
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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
public class TeamServiceTest {
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private TeamService sut;

    private UserAccount user;

    private Team team;

    @BeforeEach
    public void setup() {
        this.user = Generator.generateUser();
    }

    @Test
    public void createTeamTest() {
        String teamName = "testTeam";

        team = sut.createTeam(user, teamName);
        team.setId(1);
        em.persist(team);
        assertNotNull(team);


        assertNotNull(em.find(Team.class, team.getId()));
        assertEquals(team.getMembers().get(0).getTeamRole(), TeamRole.OWNER);
        assertEquals(team.getMembers().get(0).getUserAccount(), user);
        assertEquals(1, user.getTeamMemberships().size());
        assertEquals(1, team.getMembers().size());
    }

    @Test
    public void createTeamTestDoesntEffectOtherMemberships() {

        String teamName = "testTeam";
        String teamName2 = "testTeam2";

        Team team = sut.createTeam(user, teamName2);

        Team resultTeam = sut.createTeam(user, teamName);

        assertNotNull(resultTeam);
        assertEquals(2, user.getTeamMemberships().size());
        assertEquals(1, resultTeam.getMembers().size());
    }

    @Test
    public void dissolveTeamTest() {
        String teamName = "testTeam";
        UserAccount user1 = Generator.generateUser();
        UserAccount user2 = Generator.generateUser();

        em.persist(user1);
        em.persist(user2);

        team = sut.createTeam(user, teamName);
        team.setId(1);
        em.persist(team);

        sut.createTeamMember(team, TeamRole.MEMBER, user1);
        sut.createTeamMember(team, TeamRole.MEMBER, user2);

        assertNotNull(em.find(Team.class, team.getId()));
        assertEquals(3, team.getMembers().size());

        sut.dissolveTeam(team);

        assertNull(em.find(Team.class, team.getId()));
    }

    @Test
    public void addTeamMemberTest() {
        team = new Team();

        sut.createTeamMember(team, TeamRole.MEMBER, user);

        TeamMember teamMember = team.getMembers().get(0);
        teamMember.setId(1);

        em.persist(team);
        em.persist(user);
        em.persist(teamMember);

        assertEquals(1, team.getMembers().size());
        assertEquals(1, user.getTeamMemberships().size());
    }

    @Test
    public void removeTeamMemberTest() {
        team = new Team();

        sut.createTeamMember(team, TeamRole.MEMBER, user);

        TeamMember teamMember = user.getTeamMemberships().get(0);
        teamMember.setId(1);

        em.persist(team);
        em.persist(teamMember);

        sut.removeMember(teamMember);

        assertNull(em.find(TeamMember.class, teamMember.getId()));
        assertEquals(0, team.getMembers().size());
        assertEquals(0, user.getTeamMemberships().size());
    }
}
