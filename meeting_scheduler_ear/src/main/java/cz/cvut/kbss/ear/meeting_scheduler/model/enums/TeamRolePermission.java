package cz.cvut.kbss.ear.meeting_scheduler.model.enums;

public enum TeamRolePermission {

    //MEMBER
    CREATE_FEEDBACK,
    VIEW_TEAM_MEMBERS,
    LEAVE_TEAM,

    //MAINTAINER
    CREATE_MEETING,
    CREATE_MEETING_FROM_OFFER,
    CANCEL_MEETING,
    ADD_TEAM_MEMBER,
    KICK_TEAM_MEMBER,
    EDIT_MEETING_OFFER,

    //OWNER
    CHANGE_TEAM_ROLE,
    KICK_MAINTAINER,
    DISSOLVE_TEAM,
}
