package cz.cvut.kbss.ear.meeting_scheduler.service;

import cz.cvut.kbss.ear.meeting_scheduler.dao.MeetingDao;
import cz.cvut.kbss.ear.meeting_scheduler.model.Meeting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MeetingService {
    private final MeetingDao dao;

    @Autowired
    public MeetingService(MeetingDao dao) {
        this.dao = dao;
    }

    @Transactional
    public void createMeeting(Meeting meeting) {
        dao.persist(meeting);
    }

    @Transactional
    public void removeMeeting(Meeting meeting) {
        dao.remove(meeting);
    }

    @Transactional
    public Meeting findMeetingByID(int id) {
        return dao.find(id);
    }
}
