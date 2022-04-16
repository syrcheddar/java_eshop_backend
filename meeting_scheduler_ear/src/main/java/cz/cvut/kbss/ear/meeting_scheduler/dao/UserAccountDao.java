package cz.cvut.kbss.ear.meeting_scheduler.dao;

import cz.cvut.kbss.ear.meeting_scheduler.model.UserAccount;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import java.util.List;

@Repository
public class UserAccountDao extends BaseDao<UserAccount> {

    public UserAccountDao() {
        super(UserAccount.class);
    }

    public UserAccount findByUsername(String username) {
        try {
            return em.createNamedQuery("UserAccount.findByUsername", UserAccount.class)
                    .setParameter("username", username)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public UserAccount findByEmail(String email) {
        try {
            return em.createNamedQuery("UserAccount.findByEmail", UserAccount.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<String> viewMyTeams(String username) {
        try {
            return em.createNamedQuery("UserAccount.viewMyTeams", String.class)
                    .setParameter("username", username)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

}
