package cz.cvut.kbss.ear.meeting_scheduler.security;

import cz.cvut.kbss.ear.meeting_scheduler.model.Team;
import cz.cvut.kbss.ear.meeting_scheduler.model.TeamMember;
import cz.cvut.kbss.ear.meeting_scheduler.security.model.UserAccountDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component("MySecurityEvaluator")
public class CustomPermissionEvaluator {

    public boolean isNotNull(Principal principal) {
        return principal != null;
    }

    public boolean hasPermission(UserAccountDetails auth, String teamName, String permission) {
        if ((auth == null) || (teamName == null)) {
            return false;
        }
        Team team = null;
        for (TeamMember t : auth.getUser().getTeamMemberships()) {
            if (t.getTeam().getName().equals(teamName)) {
                team = t.getTeam();
                break;
            }
        }
        assert team != null;
        GrantedAuthority aut = auth.getAuthority(team);
        return hasPrivilege(aut, permission);
    }

    private boolean hasPrivilege(GrantedAuthority auth, String permission) {
        return permission.contains(auth.getAuthority());
    }
}
