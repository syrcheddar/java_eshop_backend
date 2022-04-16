package cz.cvut.kbss.ear.meeting_scheduler.dao;

import cz.cvut.kbss.ear.meeting_scheduler.MeetingSchedulerApplication;
import cz.cvut.kbss.ear.meeting_scheduler.environment.TestConfiguration;
import cz.cvut.kbss.ear.meeting_scheduler.model.Meeting;
import cz.cvut.kbss.ear.meeting_scheduler.model.Team;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ComponentScan(basePackageClasses = MeetingSchedulerApplication.class, excludeFilters = {
        //@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SystemInitializer.class),
        @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = TestConfiguration.class)})
public class MeetingDaoTest {
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private MeetingDao sut;

    @Test
    public void getAllMeetingsInPragueReturnsCorrectMeetings() {
        Team team = new Team("testTeam");
        Team team2 = new Team("testTeam2");
        Date meetingDate = new Date(2000, Calendar.NOVEMBER, 2);
        Date meetingDate2 = new Date(2021, Calendar.DECEMBER, 29);

        Meeting meeting1 = new Meeting(team, meetingDate, new Time(0, 0, 0), new Time(0, 0, 0), "Prague", "description1");
        Meeting meeting2 = new Meeting(team2, meetingDate, new Time(0, 0, 0), new Time(0, 0, 0), "London", "description2");
        Meeting meeting3 = new Meeting(team, meetingDate2, new Time(0, 0, 0), new Time(0, 0, 0), "Prague", "description3");

        List<Meeting> expectedList = new ArrayList<>();
        expectedList.add(meeting1);
        expectedList.add(meeting3);

        em.persist(team);
        em.persist(team2);
        em.persist(meeting1);
        em.persist(meeting2);
        em.persist(meeting3);

        List<Meeting> resultList = sut.getAllMeetingsInPrague();

        assertEquals(expectedList.size(), resultList.size());

        assertTrue(resultList.stream().anyMatch(c -> c.getLocation().equals(meeting1.getLocation())));
        assertTrue(resultList.stream().anyMatch(c -> c.getLocation().equals(meeting3.getLocation())));

        assertTrue(resultList.stream().anyMatch(c -> c.getMeetingDate().equals(meeting1.getMeetingDate())));
        assertTrue(resultList.stream().anyMatch(c -> c.getMeetingDate().equals(meeting3.getMeetingDate())));
    }

    @Test
    public void getAllMeetingsInPragueReturnsEmptyListWhenNoLocationMatch() {
        Team team = new Team("testTeam");
        Team team2 = new Team("testTeam2");
        Date meetingDate = new Date(2000, Calendar.NOVEMBER, 2);
        Date meetingDate2 = new Date(2021, Calendar.DECEMBER, 29);

        Meeting meeting1 = new Meeting(team, meetingDate, new Time(0, 0, 0), new Time(0, 0, 0), "Paris", "description1");
        Meeting meeting2 = new Meeting(team2, meetingDate, new Time(0, 0, 0), new Time(0, 0, 0), "London", "description2");
        Meeting meeting3 = new Meeting(team, meetingDate2, new Time(0, 0, 0), new Time(0, 0, 0), "Berlin", "description3");

        List<Meeting> expectedList = new ArrayList<>();

        em.persist(team);
        em.persist(team2);
        em.persist(meeting1);
        em.persist(meeting2);
        em.persist(meeting3);

        List<Meeting> resultList = sut.getAllMeetingsInPrague();

        assertEquals(0, resultList.size());
        assertEquals(expectedList, resultList);
    }

    @Test
    public void getAllMeetingsInPragueReturnsEmptyListWhenThereAreNoMeetings() {
        List<Meeting> expectedList = new ArrayList<>();

        List<Meeting> resultList = sut.getAllMeetingsInPrague();

        assertEquals(0, resultList.size());
        assertEquals(expectedList, resultList);
    }
}
