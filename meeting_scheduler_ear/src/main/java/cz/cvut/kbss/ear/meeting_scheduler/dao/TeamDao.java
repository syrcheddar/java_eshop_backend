package cz.cvut.kbss.ear.meeting_scheduler.dao;

import cz.cvut.kbss.ear.meeting_scheduler.model.Team;
import cz.cvut.kbss.ear.meeting_scheduler.model.TeamMember;
import cz.cvut.kbss.ear.meeting_scheduler.model.enums.TeamRole;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import java.util.List;

@Repository
public class TeamDao extends BaseDao<Team> {

    public TeamDao() {
        super(Team.class);
    }

    public Team findByName(String teamName) {
        try {
            return em.createNamedQuery("Team.findByName", Team.class)
                    .setParameter("name", teamName)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public TeamMember getTeamOwner(String teamName) {
        TeamRole teamRole = TeamRole.OWNER;
        try {
            return em.createNamedQuery("Team.getTeamOwner", TeamMember.class)
                    .setParameter("teamName", teamName)
                    .setParameter("teamRole", teamRole)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<String> viewTeamMembers(String teamName) {
        try {
            return em.createNamedQuery("Team.viewTeamMembers", String.class)
                    .setParameter("teamName", teamName)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
}
