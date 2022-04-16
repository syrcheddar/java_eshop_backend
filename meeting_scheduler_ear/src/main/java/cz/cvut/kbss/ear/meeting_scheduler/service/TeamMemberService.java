package cz.cvut.kbss.ear.meeting_scheduler.service;

import cz.cvut.kbss.ear.meeting_scheduler.dao.*;
import cz.cvut.kbss.ear.meeting_scheduler.exception.NotEnoughPermissionException;
import cz.cvut.kbss.ear.meeting_scheduler.exception.TeamMemberException;
import cz.cvut.kbss.ear.meeting_scheduler.model.*;
import cz.cvut.kbss.ear.meeting_scheduler.model.enums.TeamRole;
import cz.cvut.kbss.ear.meeting_scheduler.model.enums.TeamRolePermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class TeamMemberService {

    private final TeamMemberDao dao;
    private final TeamDao tDao;
    private final MeetingOfferDao moDao;
    private final MeetingDao mDao;
    private final UserAccountDao uaDao;
    private final FeedbackDao fDao;

    @Autowired
    public TeamMemberService(TeamMemberDao tmDao, TeamDao tDao, MeetingOfferDao moDao, MeetingDao mDao, UserAccountDao uaDao, FeedbackDao fDao) {
        this.mDao = mDao;
        this.dao = tmDao;
        this.tDao = tDao;
        this.moDao = moDao;
        this.uaDao = uaDao;
        this.fDao = fDao;
    }

    // MEMBER
    @Transactional
    public void createFeedback(TeamMember teamMember, Feedback feedback, MeetingOption option, MeetingOffer offer) {
        Objects.requireNonNull(offer);
        Objects.requireNonNull(option);
        Team team = teamMember.getTeam();
        if (teamMember.getTeamRole().hasPermision(TeamRolePermission.CREATE_FEEDBACK) && offer.getTeam().getId().equals(team.getId())) {
            Objects.requireNonNull(feedback);

//            feedback.setMeetingOption(option);
            option.addFeedBack(feedback);

            moDao.update(offer);

        } else throw new NotEnoughPermissionException("Permission Denied!");
    }

    @Transactional
    public List<String> viewTeamMembers(TeamMember teamMember) {
        if (teamMember.getTeamRole().hasPermision(TeamRolePermission.VIEW_TEAM_MEMBERS)) {
            return tDao.viewTeamMembers(teamMember.getTeam().getName());
        }
        throw new NotEnoughPermissionException("Permission Denied!");
    }

    @Transactional
    public void leaveTeam(TeamMember teamMember) {
        Team team = teamMember.getTeam();
        UserAccount userAccount = teamMember.getUserAccount();

        team.removeMember(teamMember);
        userAccount.removeTeamMemberShip(teamMember);

        tDao.update(team);
        uaDao.update(userAccount);

        teamMember.setTeam(null);
        teamMember.setUserAccount(null);

        dao.update(teamMember);

        dao.remove(teamMember);

    }

    //MAINTAINER
    @Transactional
    public void createMeetingOffer(TeamMember teamMember, List<MeetingOption> meetingOptions) {
        if (teamMember.getTeamRole().hasPermision(TeamRolePermission.CREATE_MEETING)) {
            Objects.requireNonNull(meetingOptions);
            Objects.requireNonNull(teamMember);

            Team team = teamMember.getTeam();
            MeetingOffer mo = new MeetingOffer(team, meetingOptions);
            team.addMeetingOffer(mo);

            moDao.persist(mo);
            tDao.update(team);
        } else throw new NotEnoughPermissionException("Permission Denied!");
    }

    @Transactional
    public void createMeetingFromOffer(TeamMember teamMember, MeetingOffer offer, MeetingOption chosenOption) {
        Objects.requireNonNull(offer);
        Objects.requireNonNull(teamMember);
        Team team = teamMember.getTeam();
        if (teamMember.getTeamRole().hasPermision(TeamRolePermission.CREATE_MEETING_FROM_OFFER) && offer.getTeam().getId().equals(team.getId())) {
            Objects.requireNonNull(chosenOption);

            team.removeMeetingOffer(offer);

            ArrayList<Feedback> feedbacks = new ArrayList<>();
            for (MeetingOption o : offer.getOptions()) {
                feedbacks.addAll(o.getFeedBackList());
                o.setFeedBackList(new ArrayList<>());
            }
            for (int i = 0; i < offer.getOptions().size(); i++) {
                mDao.remove(offer.getOptions().get(i));
                offer.getOptions().get(0).setFeedBackList(null);
            }
            for (int i = 0; i < feedbacks.size(); i++) {
                fDao.remove(feedbacks.get(0));
            }
            offer.setOptions(null);
            moDao.remove(offer);

            Meeting meeting = new Meeting(chosenOption.getTeam(), chosenOption.getMeetingDate(), chosenOption.getTimeFrom(), chosenOption.getTimeTo(), chosenOption.getLocation(), chosenOption.getDescription());
            meeting.setTeam(team);
            team.addMeeting(meeting);
            mDao.persist(meeting);
            tDao.update(team);
        } else throw new NotEnoughPermissionException("Permission Denied!");
    }

    @Transactional
    public void cancelMeeting(TeamMember teamMember, Meeting meeting) {
        Objects.requireNonNull(meeting);
        Objects.requireNonNull(teamMember);
        Team team = teamMember.getTeam();
        if (teamMember.getTeamRole().hasPermision(TeamRolePermission.CANCEL_MEETING) && meeting.getTeam().getId().equals(team.getId())) {

            Meeting m = team.removeMeeting(meeting);
            mDao.remove(m);
            tDao.update(team);
        } else throw new NotEnoughPermissionException("Permission Denied!");
    }

    @Transactional
    public void cancelMeetingOffer(TeamMember teamMember, MeetingOffer meetingOffer) {
        Objects.requireNonNull(meetingOffer);
        Objects.requireNonNull(teamMember);
        Team team = teamMember.getTeam();
        if (teamMember.getTeamRole().hasPermision(TeamRolePermission.CANCEL_MEETING) && meetingOffer.getTeam().getId().equals((team).getId())) {


            ArrayList<Feedback> feedbacks = new ArrayList<>();
            for (MeetingOption o : meetingOffer.getOptions()) {
                feedbacks.addAll(o.getFeedBackList());
                o.setFeedBackList(new ArrayList<>());
            }
            moDao.remove(meetingOffer);
            for (int i = 0; i < meetingOffer.getOptions().size(); i++) {
                mDao.remove(meetingOffer.getOptions().get(0));
            }
            for (int i = 0; i < feedbacks.size(); i++) {
                fDao.remove(feedbacks.get(0));
            }

            team.removeMeetingOffer(meetingOffer);

            tDao.update(team);
        } else throw new NotEnoughPermissionException("Permission Denied!");
    }

    @Transactional
    public void addTeamMember(TeamMember teamMember, UserAccount account, TeamRole role) {
        if (teamMember.getTeamRole().hasPermision(TeamRolePermission.ADD_TEAM_MEMBER)) {
            if (!role.equals(TeamRole.OWNER)) {
                if (teamMember.getTeamRole().equals(TeamRole.OWNER) || !role.equals(TeamRole.MAINTAINER)) {
                    Team team = teamMember.getTeam();
                    Objects.requireNonNull(account);
                    Objects.requireNonNull(team);
                    Objects.requireNonNull(role);

                    TeamService tService = new TeamService(tDao, dao, uaDao);
                    tService.createTeamMember(team, role, account);
                }
            }
        } else throw new NotEnoughPermissionException("Permission Denied!");
    }

    @Transactional
    public void kickTeamMember(TeamMember teamMemberWPermissions, TeamMember memberToKick) {
        if (teamMemberWPermissions.getTeamRole().hasPermision(TeamRolePermission.KICK_TEAM_MEMBER) && memberToKick.getTeamRole().equals(TeamRole.MEMBER)) {
            Team team = teamMemberWPermissions.getTeam();
            Objects.requireNonNull(memberToKick);
            Objects.requireNonNull(team);

            TeamService tService = new TeamService(tDao, dao, uaDao);
            tService.removeMember(memberToKick);
            dao.remove(memberToKick);
        } else throw new NotEnoughPermissionException("Permission Denied!");
    }

    //OWNER

    @Transactional
    public void changeTeamRole(TeamMember teamMemberWPermissions, TeamMember memberToChangeRole, TeamRole role) {
        Objects.requireNonNull(memberToChangeRole);
        if (teamMemberWPermissions.getTeamRole().hasPermision(TeamRolePermission.CHANGE_TEAM_ROLE) && memberToChangeRole.getTeam().equals(teamMemberWPermissions.getTeam())) {
            Objects.requireNonNull(role);
            if (role != TeamRole.OWNER) {
                memberToChangeRole.setTeamRole(role);
                dao.update(memberToChangeRole);
            } else throw new TeamMemberException("You cannot make somebody else owner!");
        } else throw new NotEnoughPermissionException("Permission Denied!");
    }

    @Transactional
    public void kickMaintainer(TeamMember teamMemberWPermissions, TeamMember member) {
        Objects.requireNonNull(member);
        if (teamMemberWPermissions.getTeamRole().hasPermision(TeamRolePermission.KICK_MAINTAINER) && member.getTeam().equals(teamMemberWPermissions.getTeam())) {
            TeamService tService = new TeamService(tDao, dao, uaDao);
            tService.removeMember(member);
        } else throw new NotEnoughPermissionException("Permission Denied!");
    }

    @Transactional
    public void createTeam(UserAccount userAccount, String teamName) {
        TeamService teamService = new TeamService(tDao, dao, uaDao);
        teamService.createTeam(userAccount, teamName);
    }

    @Transactional
    public void dissolveTeam(TeamMember teamMemberWPermissions) {
        if (teamMemberWPermissions.getTeamRole().hasPermision(TeamRolePermission.DISSOLVE_TEAM)) {

            Team team = teamMemberWPermissions.getTeam();

            while (team.getMembers().size() != 0) {
                leaveTeam(team.getMembers().get(0));
            }
            tDao.update(team);
            TeamService tService = new TeamService(tDao, dao, uaDao);
            tService.dissolveTeam(team);

        } else throw new NotEnoughPermissionException("Permission Denied!");
    }
}
