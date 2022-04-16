package cz.cvut.kbss.ear.meeting_scheduler.service;

import cz.cvut.kbss.ear.meeting_scheduler.dao.UserAccountDao;
import cz.cvut.kbss.ear.meeting_scheduler.model.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserAccountService {

    private final UserAccountDao dao;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserAccountService(UserAccountDao dao, PasswordEncoder passwordEncoder) {
        this.dao = dao;
        this.passwordEncoder = passwordEncoder;
    }

    public List<String> viewProfileInfo(UserAccount userAccount) {
        List<String> userInfo = new ArrayList<>();
        userInfo.add(userAccount.getUsername());
        userInfo.add(userAccount.getEmail());

        return userInfo;
    }

    @Transactional(readOnly = true)
    public List<String> viewMyTeams(UserAccount userAccount) {
        return dao.viewMyTeams(userAccount.getUsername());
    }

    @Transactional
    public void createAccount(UserAccount userAccount) {
        userAccount.encodePassword(passwordEncoder);
        dao.persist(userAccount);
    }

    @Transactional
    public void removeAccount(UserAccount userAccount) {
        dao.remove(userAccount);
    }

    @Transactional
    public void updateAccount(UserAccount userAccount) {
        dao.update(userAccount);
    }

    public UserAccount findUserByUsername(String username) {
        return dao.findByUsername(username);
    }
}
