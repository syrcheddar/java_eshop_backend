package cz.cvut.kbss.ear.meeting_scheduler.model;

import javax.persistence.*;
import java.sql.Time;
import java.util.Date;

@Entity
@Table(name = "MEETING")
@SqlResultSetMapping(
        name = "MeetingResult",
        classes = {
                @ConstructorResult(
                        targetClass = Meeting.class,
                        columns = {
                                @ColumnResult(name = "team", type = Team.class),
                                @ColumnResult(name = "meetingDate", type = Date.class),
                                @ColumnResult(name = "timeFrom", type = Time.class),
                                @ColumnResult(name = "timeTo", type = Time.class),
                                @ColumnResult(name = "location", type = String.class),
                                @ColumnResult(name = "description", type = String.class)
                        }
                )
        }
)
@NamedNativeQueries({
        @NamedNativeQuery(
                name = "Meeting.getAllMeetingsInPrague",
                query = "SELECT * FROM Meeting WHERE location = 'Prague'",
                resultSetMapping = "MeetingResult"
        )
})
public class Meeting extends AbstractEntity {
    @ManyToOne
    private Team team;

    private Date meetingDate;
    private Time timeFrom;
    private Time timeTo;
    private String location;
    private String description;

    public Meeting() {
    }

    public Meeting(Team team, Date meetingDate, Time timeFrom, Time timeTo, String location, String description) {
        this.team = team;
        this.meetingDate = meetingDate;
        this.timeFrom = timeFrom;
        this.timeTo = timeTo;
        this.location = location;
        this.description = description;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Date getMeetingDate() {
        return meetingDate;
    }

    public void setMeetingDate(Date meetingDate) {
        this.meetingDate = meetingDate;
    }

    public Time getTimeFrom() {
        return timeFrom;
    }

    public void setTimeFrom(Time timeFrom) {
        this.timeFrom = timeFrom;
    }

    public Time getTimeTo() {
        return timeTo;
    }

    public void setTimeTo(Time timeTo) {
        this.timeTo = timeTo;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
