package cz.cvut.kbss.ear.meeting_scheduler.service;

import cz.cvut.kbss.ear.meeting_scheduler.dao.MeetingOfferDao;
import cz.cvut.kbss.ear.meeting_scheduler.model.MeetingOffer;
import cz.cvut.kbss.ear.meeting_scheduler.model.MeetingOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MeetingOfferService {
    private final MeetingOfferDao dao;

    @Autowired
    public MeetingOfferService(MeetingOfferDao dao) {
        this.dao = dao;
    }

    @Transactional
    public void createOffer(MeetingOffer offer) {
        dao.persist(offer);
    }

    @Transactional
    public void removeOffer(MeetingOffer offer) {
        dao.remove(offer);
    }

    @Transactional
    public void updateOffer(MeetingOffer offer) {
        dao.update(offer);
    }

    @Transactional
    public void editOffer(MeetingOffer meetingOfferToEdit, MeetingOffer editedMeetingOffer) {
        dao.remove(meetingOfferToEdit);
        dao.persist(editedMeetingOffer);
    }

    @Transactional
    public MeetingOption findOptionByID(int id) {
        return dao.findOptionById(id);
    }

    @Transactional
    public MeetingOffer findOfferByID(int id) {
        return dao.find(id);
    }
}
