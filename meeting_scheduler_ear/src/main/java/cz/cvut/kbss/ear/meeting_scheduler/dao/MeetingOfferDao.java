package cz.cvut.kbss.ear.meeting_scheduler.dao;

import cz.cvut.kbss.ear.meeting_scheduler.model.MeetingOffer;
import cz.cvut.kbss.ear.meeting_scheduler.model.MeetingOption;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;

@Repository
public class MeetingOfferDao extends BaseDao<MeetingOffer> {

    public MeetingOfferDao() {
        super(MeetingOffer.class);
    }

    public MeetingOption findOptionById(int id) {
        try {
            return em.createNamedQuery("MeetingOffer.findOptionByID", MeetingOption.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
