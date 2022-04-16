package cz.cvut.kbss.ear.meeting_scheduler.model;

import cz.cvut.kbss.ear.meeting_scheduler.exception.TeamMemberException;
import cz.cvut.kbss.ear.meeting_scheduler.exception.UserAccountException;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "USER_ACCOUNT")
@NamedQueries({
        @NamedQuery(name = "UserAccount.findByUsername", query = "SELECT u FROM UserAccount u WHERE u.username = :username"),
        @NamedQuery(name = "UserAccount.findByEmail", query = "SELECT u FROM UserAccount u WHERE u.email = :email"),
        @NamedQuery(name = "UserAccount.viewMyTeams", query = "SELECT t.name FROM Team t JOIN TeamMember tm WHERE tm.userAccount.username = :username AND t.id = tm.team.id")
})
public class UserAccount extends AbstractEntity {

    @Basic(optional = false)
    @Column(nullable = false)
    private String username;

    @Basic(optional = false)
    @Column(nullable = false)
    private String password;
    private String email;
    private boolean isBlocked;

    @OneToMany(fetch = FetchType.LAZY)
    @OrderBy("team")
    private List<TeamMember> teamMemberships;

    public UserAccount(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.isBlocked = false;
        this.teamMemberships = new ArrayList<>();
    }

    public UserAccount() {
    }

    public void addTeamMembership(TeamMember member) {
        Objects.requireNonNull(member);
        if (teamMemberships == null) {
            teamMemberships = new ArrayList<>();
        }
        for (TeamMember a : teamMemberships) {
            if (a.equals(member)) {
                throw new UserAccountException("Account is already in team");

            }
        }
        teamMemberships.add(member);
    }

    public void removeTeamMemberShip(TeamMember member) {
        boolean isRemoved = false;
        if (teamMemberships == null) {
            throw new TeamMemberException("Account is not in team.");
        }
        for (int i = 0; i < teamMemberships.size(); i++) {
            if (teamMemberships.get(i).getTeam().equals(member.getTeam())) {
                teamMemberships.remove(i);
                isRemoved = true;
                break;
            }
        }
        if (!isRemoved) {
            throw new TeamMemberException("Account is not in team.");
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public List<TeamMember> getTeamMemberships() {
        return teamMemberships;
    }

    public void setTeamMemberships(List<TeamMember> teamMemberships) {
        Objects.requireNonNull(teamMemberships);
        this.teamMemberships = teamMemberships;
    }

    public void erasePassword() {
        this.password = null;
    }

    public void encodePassword(PasswordEncoder encoder) {
        this.password = encoder.encode(password);
    }
}
