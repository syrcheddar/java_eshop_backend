package cz.cvut.kbss.ear.meeting_scheduler.service;

import cz.cvut.kbss.ear.meeting_scheduler.model.MeetingOffer;
import cz.cvut.kbss.ear.meeting_scheduler.model.MeetingOption;
import cz.cvut.kbss.ear.meeting_scheduler.model.Team;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
public class MeetingOfferServiceTest {
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private MeetingOfferService sut;

    @Test
    public void editOfferTest() {
        Team team = new Team();
        Team team2 = new Team();

        MeetingOffer meetingOffer = new MeetingOffer();
        meetingOffer.setTeam(team);
        meetingOffer.setOptions(new ArrayList<>());

        em.persist(meetingOffer);

        MeetingOption meetingOption = new MeetingOption();
        em.persist(meetingOption);
        ArrayList<MeetingOption> meetingOptions = new ArrayList<>();
        meetingOptions.add(meetingOption);

        MeetingOffer meetingOffer2 = new MeetingOffer();
        meetingOffer2.setTeam(team2);
        meetingOffer2.setOptions(meetingOptions);

        sut.editOffer(meetingOffer, meetingOffer2);

        assertNotEquals(meetingOffer, meetingOffer2);
        assertNull(em.find(MeetingOffer.class, meetingOffer.getId()));
        assertNotNull(em.find(MeetingOffer.class, meetingOffer2.getId()));
    }
}
