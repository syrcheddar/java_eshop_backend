package cz.cvut.kbss.ear.meeting_scheduler.dao;

import cz.cvut.kbss.ear.meeting_scheduler.MeetingSchedulerApplication;
import cz.cvut.kbss.ear.meeting_scheduler.environment.Generator;
import cz.cvut.kbss.ear.meeting_scheduler.exception.PersistenceException;
import cz.cvut.kbss.ear.meeting_scheduler.model.UserAccount;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ComponentScan(basePackageClasses = MeetingSchedulerApplication.class)
public class BaseDaoTest {
    @Autowired
    private TestEntityManager em;

    @Autowired
    private UserAccountDao sut;

    @Test
    public void persistSavesSpecifiedInstance() {
        UserAccount user = Generator.generateUser();
        sut.persist(user);

        UserAccount resultUser = em.find(UserAccount.class, user.getId());

        assertNotNull(user.getId());
        assertEquals(user.getId(), resultUser.getId());
        assertEquals(user.getUsername(), resultUser.getUsername());
    }

    @Test
    public void findRetrievesInstanceByIdentifier() {
        UserAccount user = Generator.generateUser();
        sut.persist(user);

        UserAccount resultUser = sut.find(user.getId());

        assertNotNull(user);
        assertNotNull(resultUser);
        assertEquals(user.getId(), resultUser.getId());
        assertEquals(user.getUsername(), resultUser.getUsername());
    }

    @Test
    public void findAllRetrievesAllInstancesOfType() {
        UserAccount user = Generator.generateUser();
        UserAccount user2 = Generator.generateUser();
        sut.persist(user);
        sut.persist(user2);

        List<UserAccount> resultList = sut.findAll();

        assertEquals(2, resultList.size());
        assertTrue(resultList.stream().anyMatch(c -> c.getId().equals(user.getId())));
        assertTrue(resultList.stream().anyMatch(c -> c.getId().equals(user2.getId())));
    }

    @Test
    public void updateUpdatesExistingInstance() {
        UserAccount user = Generator.generateUser();
        sut.persist(user);

        UserAccount update = new UserAccount();
        update.setId(user.getId());
        String newName = "New User Name";
        update.setUsername(newName);

        sut.update(update);

        UserAccount resultUser = sut.find(user.getId());

        assertNotNull(resultUser);
        assertEquals(newName, resultUser.getUsername());
    }

    @Test
    public void removeRemovesSpecifiedInstance() {
        UserAccount user = Generator.generateUser();
        em.persist(user);
        assertNotNull(em.find(UserAccount.class, user.getId()));

        em.detach(user);
        sut.remove(user);

        assertNull(em.find(UserAccount.class, user.getId()));
    }

    @Test
    public void removeDoesNothingWhenInstanceDoesNotExist() {
        UserAccount user = Generator.generateUser();
        assertNull(em.find(UserAccount.class, user.getId()));

        sut.remove(user);
        assertNull(em.find(UserAccount.class, user.getId()));

    }

    @Test
    public void exceptionOnPersistInWrappedInPersistenceException() {
        UserAccount user = Generator.generateUser();
        em.persistAndFlush(user);
        em.remove(user);

        assertThrows(PersistenceException.class, () -> sut.update(user));
    }

    @Test
    public void existsReturnsTrueForExistingIdentifier() {
        UserAccount user = Generator.generateUser();
        em.persistAndFlush(user);

        assertTrue(sut.exists(user.getId()));
        assertFalse(sut.exists(-1));
    }
}
