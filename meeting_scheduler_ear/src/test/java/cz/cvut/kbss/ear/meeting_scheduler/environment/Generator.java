package cz.cvut.kbss.ear.meeting_scheduler.environment;

import cz.cvut.kbss.ear.meeting_scheduler.model.Team;
import cz.cvut.kbss.ear.meeting_scheduler.model.TeamMember;
import cz.cvut.kbss.ear.meeting_scheduler.model.UserAccount;
import cz.cvut.kbss.ear.meeting_scheduler.model.enums.TeamRole;

import java.util.Random;

public class Generator {

    private static final Random RAND = new Random();

    public static int randomInt() {
        return RAND.nextInt();
    }

    public static UserAccount generateUser() {
        final UserAccount user = new UserAccount();
        user.setId(randomInt());
        user.setEmail("FirstName" + randomInt());
        user.setUsername("username" + randomInt() + "@kbss.felk.cvut.cz");
        user.setPassword(Integer.toString(randomInt()));
        return user;
    }

    public static TeamMember generateMember() {
        final TeamMember member = new TeamMember();
        member.setId(randomInt());
        member.setUserAccount(generateUser());
        member.setTeamRole(TeamRole.MEMBER);
        return member;
    }

    public static Team generateTeam() {
        final Team t = new Team();
        t.setId(randomInt());
        t.setName("Team" + randomInt());
        for (int i = 0; i < 10; i++) {
            UserAccount user = generateUser();
            TeamMember teamMember = new TeamMember(user, t, TeamRole.MEMBER);
            t.addMember(teamMember);
        }
        return t;
    }
}
