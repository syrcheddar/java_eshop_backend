package cz.cvut.kbss.ear.meeting_scheduler.dao;

import cz.cvut.kbss.ear.meeting_scheduler.model.TeamMember;
import org.springframework.stereotype.Repository;

@Repository
public class TeamMemberDao extends BaseDao<TeamMember> {
    public TeamMemberDao() {
        super(TeamMember.class);
    }
}
