package cz.cvut.kbss.ear.meeting_scheduler.model;

import cz.cvut.kbss.ear.meeting_scheduler.exception.EarException;
import cz.cvut.kbss.ear.meeting_scheduler.exception.TeamMemberException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "TEAM")
@NamedQueries({
        @NamedQuery(name = "Team.findByName", query = "SELECT t FROM Team t WHERE t.name = :name"),
        @NamedQuery(name = "Team.getTeamOwner", query = "SELECT t FROM TeamMember t WHERE t.teamRole = :teamRole AND t.team.name = :teamName"),
        @NamedQuery(name = "Team.viewTeamMembers", query = "SELECT t.userAccount.username FROM TeamMember t WHERE t.team.name = :teamName")
})
public class Team extends AbstractEntity {

    @Basic(optional = false)
    @Column(nullable = false)
    private String name;

    @OneToMany(cascade = CascadeType.PERSIST)
    @OrderBy("userAccount")
    private List<TeamMember> members;

    @OneToMany
    private List<MeetingOffer> meetingOffers;

    @OneToMany
    @OrderBy("timeFrom")
    private List<Meeting> meetings;

    public Team() {
    }

    public Team(String name) {
        this.name = name;
    }

    public void addMember(TeamMember tm) {
        Objects.requireNonNull(tm);

        if (members == null) {
            this.members = new ArrayList<>();
        }
        for (TeamMember teamMember : members) {
            if (teamMember.equals(tm)) {
                throw new TeamMemberException("Account is already in team!");
            }
        }
        members.add(tm);
    }

    public void removeMember(TeamMember member) {
        Objects.requireNonNull(member);
        if (members != null) {
            boolean removed = false;
            for (int i = 0; i < members.size(); i++) {
                if (members.get(i).getUserAccount().getUsername().equals(member.getUserAccount().getUsername())) {
                    members.remove(i);
                    removed = true;
                    break;
                }
            }
            if (!removed) {
                throw new TeamMemberException("Account is not in team");
            }
        } else throw new TeamMemberException("Account is not in team");
    }

    public void addMeetingOffer(MeetingOffer meetingOffer) {
        if (this.meetingOffers == null) {
            this.meetingOffers = new ArrayList<>();
        }
        meetingOffers.add(meetingOffer);
    }

    public void removeMeetingOffer(MeetingOffer meetingOffer) {
        for (int i = 0; i < meetingOffers.size(); i++) {
            if (meetingOffers.get(i).getId().equals(meetingOffer.getId())) {
                meetingOffers.remove(i);
                return;
            }
        }
        throw new EarException("Offer is not in offers");

    }

    public void addMeeting(Meeting meeting) {
        if (this.meetings == null) {
            this.meetings = new ArrayList<>();
        }
        meetings.add(meeting);
    }

    public Meeting removeMeeting(Meeting meeting) {
        for (int i = 0; i < meetings.size(); i++) {
            if (meetings.get(i).getId().equals(meeting.getId())) {
                Meeting m = meetings.get(i);
                meetings.remove(m);
                return m;
            }
        }
        throw new EarException("Meeting is not in meetings");
    }

    public List<MeetingOffer> getMeetingOffers() {
        return meetingOffers;
    }

    public void setMeetingOffers(List<MeetingOffer> meetingOffers) {
        this.meetingOffers = meetingOffers;
    }

    public List<Meeting> getMeetings() {
        return meetings;
    }

    public void setMeetings(List<Meeting> meetings) {
        this.meetings = meetings;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TeamMember> getMembers() {
        return members;
    }

    public void setMembers(List<TeamMember> members) {
        this.members = members;
    }
}
