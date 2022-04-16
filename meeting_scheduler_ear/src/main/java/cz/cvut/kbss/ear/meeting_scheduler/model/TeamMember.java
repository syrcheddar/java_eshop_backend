package cz.cvut.kbss.ear.meeting_scheduler.model;

import cz.cvut.kbss.ear.meeting_scheduler.model.enums.TeamRole;

import javax.persistence.*;

@Entity
@Table(name = "TEAM_MEMBER")
public class TeamMember extends AbstractEntity {
    @ManyToOne(cascade = CascadeType.MERGE)
    private UserAccount userAccount;

    @ManyToOne(cascade = CascadeType.MERGE)
    private Team team;

    @Enumerated
    private TeamRole teamRole;

    public TeamMember(UserAccount userAccount, Team team, TeamRole teamRole) {
        this.userAccount = userAccount;
        this.team = team;
        this.teamRole = teamRole;
    }

    public TeamMember() {
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public TeamRole getTeamRole() {
        return teamRole;
    }

    public void setTeamRole(TeamRole teamRole) {
        this.teamRole = teamRole;
    }
}
