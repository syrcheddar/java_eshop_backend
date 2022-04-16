package cz.cvut.kbss.ear.meeting_scheduler.dao;

import cz.cvut.kbss.ear.meeting_scheduler.model.Feedback;
import org.springframework.stereotype.Repository;

@Repository
public class FeedbackDao extends BaseDao<Feedback> {
    public FeedbackDao() {
        super(Feedback.class);
    }
}
