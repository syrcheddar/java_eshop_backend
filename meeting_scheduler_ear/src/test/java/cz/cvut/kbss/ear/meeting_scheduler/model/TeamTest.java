package cz.cvut.kbss.ear.meeting_scheduler.model;

import cz.cvut.kbss.ear.meeting_scheduler.environment.Generator;
import cz.cvut.kbss.ear.meeting_scheduler.exception.TeamMemberException;
import cz.cvut.kbss.ear.meeting_scheduler.model.enums.TeamRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TeamTest {
    Team team;
    UserAccount user;

    @BeforeEach
    public void setup() {
        team = new Team();
        user = Generator.generateUser();
    }

    @Test
    public void addMemberTest() {
        TeamMember teamMember = new TeamMember(user, team, TeamRole.MEMBER);
        team.addMember(teamMember);

        TeamMember resultMemberInTeam = team.getMembers().get(0);
        TeamRole resultRole = team.getMembers().get(0).getTeamRole();

        assertEquals(teamMember, resultMemberInTeam);
        assertEquals(TeamRole.MEMBER, resultRole);
    }

    @Test
    public void addMemberThatAlreadyIsInTeamTest() {
        TeamMember teamMember = new TeamMember(user, team, TeamRole.MEMBER);
        team.addMember(teamMember);

        assertThrows(TeamMemberException.class, () ->
                team.addMember(teamMember));
    }

    @Test
    public void removeUserTest() {
        TeamMember teamMember = new TeamMember(user, team, TeamRole.MEMBER);
        team.addMember(teamMember);

        team.removeMember(teamMember);

        assertEquals(team.getMembers(), new ArrayList<>());
    }

    @Test
    public void removeUserNotInTeamTest() {
        Team team1 = new Team();
        TeamMember teamMember = new TeamMember(user, team1, TeamRole.MEMBER);
        team1.addMember(teamMember);

        assertThrows(TeamMemberException.class, () -> team.removeMember(teamMember));
    }
}
