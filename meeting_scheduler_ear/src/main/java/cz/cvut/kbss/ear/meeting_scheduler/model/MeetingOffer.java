package cz.cvut.kbss.ear.meeting_scheduler.model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "MEETING_OFFER")
@NamedQueries({
        @NamedQuery(name = "MeetingOffer.findOptionByID", query = "SELECT o FROM MeetingOption o WHERE o.id = :id")
})
public class MeetingOffer extends AbstractEntity {
    @ManyToOne(cascade = CascadeType.MERGE)
    private Team team;

    @OneToMany(mappedBy = "offer", cascade = CascadeType.PERSIST)
    private List<MeetingOption> options;

    public MeetingOffer(Team team, List<MeetingOption> options) {
        this.team = team;
        this.options = options;
    }

    public MeetingOffer() {
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public List<MeetingOption> getOptions() {
        return options;
    }

    public void setOptions(List<MeetingOption> options) {
        this.options = options;
    }

    public void editOption(MeetingOption option, int index) {
        options.add(index, option);
    }
}
