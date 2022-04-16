package cz.cvut.kbss.ear.meeting_scheduler.service.security;

import cz.cvut.kbss.ear.meeting_scheduler.dao.UserAccountDao;
import cz.cvut.kbss.ear.meeting_scheduler.model.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserAccountDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UserAccountDao userAccountDao;

    @Autowired
    public UserAccountDetailsService(UserAccountDao userAccountDao) {
        this.userAccountDao = userAccountDao;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final UserAccount userAccount = userAccountDao.findByUsername(username);
        if (userAccount == null) {
            throw new UsernameNotFoundException("User with username " + username + " not found.");
        }
        return new cz.cvut.kbss.ear.meeting_scheduler.security.model.UserAccountDetails(userAccount);
    }
}
