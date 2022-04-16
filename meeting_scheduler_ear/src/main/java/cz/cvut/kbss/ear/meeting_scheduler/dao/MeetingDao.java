package cz.cvut.kbss.ear.meeting_scheduler.dao;

import cz.cvut.kbss.ear.meeting_scheduler.model.Meeting;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import java.util.List;

@Repository
public class MeetingDao extends BaseDao<Meeting> {
    public MeetingDao() {
        super(Meeting.class);
    }

    public List<Meeting> getAllMeetingsInPrague() {
        try {
            return em.createNamedQuery("Meeting.getAllMeetingsInPrague", Meeting.class)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
}
