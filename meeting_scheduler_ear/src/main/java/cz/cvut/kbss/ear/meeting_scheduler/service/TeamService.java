package cz.cvut.kbss.ear.meeting_scheduler.service;

import cz.cvut.kbss.ear.meeting_scheduler.dao.TeamDao;
import cz.cvut.kbss.ear.meeting_scheduler.dao.TeamMemberDao;
import cz.cvut.kbss.ear.meeting_scheduler.dao.UserAccountDao;
import cz.cvut.kbss.ear.meeting_scheduler.model.Team;
import cz.cvut.kbss.ear.meeting_scheduler.model.TeamMember;
import cz.cvut.kbss.ear.meeting_scheduler.model.UserAccount;
import cz.cvut.kbss.ear.meeting_scheduler.model.enums.TeamRole;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class TeamService {

    private final TeamDao dao;

    private final TeamMemberDao teamMemberDao;
    private final UserAccountDao userAccountDao;

    public TeamService(TeamDao dao, TeamMemberDao teamMemberDao, UserAccountDao userAccountDao) {
        this.dao = dao;
        this.teamMemberDao = teamMemberDao;
        this.userAccountDao = userAccountDao;
    }

    @Transactional
    public Team createTeam(UserAccount userAccount, String teamName) {
        Team team = new Team(teamName);

        TeamMember teamMember = new TeamMember(userAccount, team, TeamRole.OWNER);
        userAccount.addTeamMembership(teamMember);
        team.addMember(teamMember);

        dao.persist(team);
        userAccountDao.update(userAccount);

        return team;
    }

    //vymazat z usera odkaz na teammember
    //vymazat z team odkaz na teammember
    //vymazat teammember
    //vymazat team

    @Transactional
    public void dissolveTeam(Team team) {
        dao.remove(team);
    }

    @Transactional
    public void createTeamMember(Team team, TeamRole role, UserAccount userAccount) {
        TeamMember teamMember = new TeamMember(userAccount, team, role);
        userAccount.addTeamMembership(teamMember);
        team.addMember(teamMember);

        teamMemberDao.persist(teamMember);
        dao.update(team);
        userAccountDao.update(userAccount);

    }

    @Transactional
    public void removeMember(TeamMember member) {
        UserAccount userAccount = member.getUserAccount();
        Team team = member.getTeam();

        team.removeMember(member);
        userAccount.removeTeamMemberShip(member);

        dao.update(team);
        userAccountDao.update(userAccount);
        teamMemberDao.remove(member);
    }

    @Transactional
    public void updateTeam(Team team) {
        dao.update(team);
    }
}
