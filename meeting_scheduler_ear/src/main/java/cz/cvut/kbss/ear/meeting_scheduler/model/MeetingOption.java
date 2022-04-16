package cz.cvut.kbss.ear.meeting_scheduler.model;

import javax.persistence.*;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@DiscriminatorValue("MEETING")
public class MeetingOption extends Meeting {

    @ManyToOne
    MeetingOffer offer;
    @OneToMany(cascade = CascadeType.PERSIST)
    @OrderBy("rating")
    private List<Feedback> feedBackList;

    public MeetingOption(Team team, Date meetingDate, Time timeFrom, Time timeTo, String location, String description) {
        super(team, meetingDate, timeFrom, timeTo, location, description);
        this.feedBackList = new ArrayList<>();
    }

    public MeetingOption() {
        super();
    }

    public void addFeedBack(Feedback feedBack) {
        if (feedBackList == null) {
            feedBackList = new ArrayList<>();
        }
        this.feedBackList.add(feedBack);
    }

    public List<Feedback> getFeedBackList() {
        return feedBackList;
    }

    public void setFeedBackList(List<Feedback> feedBackList) {
        this.feedBackList = feedBackList;
    }

    public MeetingOffer getOffer() {
        return offer;
    }

    public void setOffer(MeetingOffer offer) {
        this.offer = offer;
    }
}
