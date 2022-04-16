package cz.cvut.kbss.ear.meeting_scheduler.model.enums;

import java.util.Set;

public enum TeamRole {
    MEMBER(Set.of(
            TeamRolePermission.CREATE_FEEDBACK,
            TeamRolePermission.VIEW_TEAM_MEMBERS,
            TeamRolePermission.LEAVE_TEAM)),
    MAINTAINER(Set.of(
            TeamRolePermission.CREATE_FEEDBACK,
            TeamRolePermission.VIEW_TEAM_MEMBERS,
            TeamRolePermission.LEAVE_TEAM,
            TeamRolePermission.CREATE_MEETING,
            TeamRolePermission.CREATE_MEETING_FROM_OFFER,
            TeamRolePermission.CANCEL_MEETING,
            TeamRolePermission.ADD_TEAM_MEMBER,
            TeamRolePermission.EDIT_MEETING_OFFER,
            TeamRolePermission.KICK_TEAM_MEMBER)),
    OWNER(Set.of(
            TeamRolePermission.CREATE_FEEDBACK,
            TeamRolePermission.VIEW_TEAM_MEMBERS,
            TeamRolePermission.LEAVE_TEAM,
            TeamRolePermission.CREATE_MEETING,
            TeamRolePermission.CREATE_MEETING_FROM_OFFER,
            TeamRolePermission.CANCEL_MEETING,
            TeamRolePermission.ADD_TEAM_MEMBER,
            TeamRolePermission.KICK_TEAM_MEMBER,
            TeamRolePermission.CHANGE_TEAM_ROLE,
            TeamRolePermission.KICK_MAINTAINER,
            TeamRolePermission.EDIT_MEETING_OFFER,
            TeamRolePermission.DISSOLVE_TEAM));

    private final Set<TeamRolePermission> permissions;

    TeamRole(Set<TeamRolePermission> permissions) {
        this.permissions = permissions;
    }

    public boolean hasPermision(TeamRolePermission permission) {
        return permissions.contains(permission);
    }


    public Set<TeamRolePermission> getPermissions() {
        return permissions;
    }
}
