package cz.cvut.kbss.ear.meeting_scheduler.security.model;

import cz.cvut.kbss.ear.meeting_scheduler.exception.TeamMemberException;
import cz.cvut.kbss.ear.meeting_scheduler.model.Team;
import cz.cvut.kbss.ear.meeting_scheduler.model.TeamMember;
import cz.cvut.kbss.ear.meeting_scheduler.model.UserAccount;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.*;

public class UserAccountDetails implements org.springframework.security.core.userdetails.UserDetails {

    private UserAccount user;
    private String password;


    private Set<GrantedAuthority> authorities = new HashSet<>();

    public UserAccountDetails(UserAccount user) {
        Objects.requireNonNull(user);
        this.user = user;
        password = user.getPassword();
        authorities.add(new SimpleGrantedAuthority("MAINTAINER"));
        authorities.add(new SimpleGrantedAuthority("OWNER"));
        authorities.add(new SimpleGrantedAuthority("MEMBER"));

    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.unmodifiableCollection(authorities);
    }

    public GrantedAuthority getAuthority(Team team) {
        for (TeamMember m : team.getMembers()) {
            if (m.getUserAccount().equals(user)) {
                return new SimpleGrantedAuthority(m.getTeamRole().toString());
            }
        }
        throw new TeamMemberException("This user account is not in this team");
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public UserAccount getUser() {
        return user;
    }

    public void eraseCredentials() {
        password = null;
    }
}
