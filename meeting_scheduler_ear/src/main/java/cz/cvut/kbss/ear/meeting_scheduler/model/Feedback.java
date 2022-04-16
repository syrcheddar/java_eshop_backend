package cz.cvut.kbss.ear.meeting_scheduler.model;

import cz.cvut.kbss.ear.meeting_scheduler.model.enums.FeedbackRating;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "FEEDBACK")
public class Feedback extends AbstractEntity {
//    @ManyToOne
//    private MeetingOption meetingOption;

    private FeedbackRating rating;
    private String comment;

    public Feedback(FeedbackRating feedbackRating, String comment) {
        this.rating = feedbackRating;
        this.comment = comment;
    }

    public Feedback() {
    }

    public FeedbackRating getRating() {
        return rating;
    }

    public void setRating(FeedbackRating feedbackRating) {
        this.rating = feedbackRating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
