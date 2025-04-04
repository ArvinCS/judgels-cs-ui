package judgels.uriel.contest;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import judgels.jophiel.JophielClient;
import judgels.persistence.api.Page;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.bundle.ContestBundle;
import judgels.uriel.api.contest.ActiveContestsResponse;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestConfig;
import judgels.uriel.api.contest.ContestCreateData;
import judgels.uriel.api.contest.ContestDescription;
import judgels.uriel.api.contest.ContestErrors;
import judgels.uriel.api.contest.ContestUpdateData;
import judgels.uriel.api.contest.ContestsResponse;
import judgels.uriel.api.contest.module.IcpcStyleModuleConfig;
import judgels.uriel.api.contest.role.ContestRole;
import judgels.uriel.bundle.ContestBundleRoleChecker;
import judgels.uriel.bundle.ContestBundleStore;
import judgels.uriel.bundle.contestant.ContestBundleContestantStore;
import judgels.uriel.bundle.supervisor.ContestBundleSupervisorStore;
import judgels.uriel.contest.contestant.ContestContestantStore;
import judgels.uriel.contest.log.ContestLogger;
import judgels.uriel.contest.module.ContestModuleStore;
import judgels.uriel.contest.supervisor.ContestSupervisorStore;

@Path("/api/v2/contests")
public class ContestResource {
    private static final int PAGE_SIZE = 20;
    private static final int BUNDLE_PAGE_SIZE = 6;
    private static final int MAX_BOUND_AUTO_INSERT = 1000;

    @Inject protected ActorChecker actorChecker;
    @Inject protected ContestBundleStore contestBundleStore;
    @Inject protected ContestBundleSupervisorStore contestBundleSupervisorStore;
    @Inject protected ContestBundleContestantStore contestBundleContestantStore;
    @Inject protected ContestBundleRoleChecker contestBundleRoleChecker;
    @Inject protected ContestRoleChecker contestRoleChecker;
    @Inject protected ContestStore contestStore;
    @Inject protected ContestLogger contestLogger;
    @Inject protected ContestSupervisorStore contestSupervisorStore;
    @Inject protected ContestContestantStore contestContestantStore;
    @Inject protected ContestModuleStore moduleStore;
    @Inject protected ContestContestantStore contestantStore;
    @Inject protected JophielClient jophielClient;

    @Inject public ContestResource() {}

    @GET
    @Path("/{contestJid}")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public Contest getContest(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @PathParam("contestJid") String contestJid) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));

        checkAllowed(contestRoleChecker.canView(actorJid, contest));
        return contest;
    }

    @POST
    @Path("/{contestJid}")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @UnitOfWork
    public Contest updateContest(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("contestJid") String contestJid,
            ContestUpdateData data) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));

        checkAllowed(contestRoleChecker.canManage(actorJid, contest));

        if (data.getBundleJid().isPresent()) {
            Optional<ContestBundle> contestBundle = contestBundleStore.getContestBundleByJid(data.getBundleJid().get());
            if (!contestBundle.isPresent()) {
                throw ContestErrors.bundleDoesNotExist(data.getBundleJid().get());
            }

            checkAllowed(contestBundleRoleChecker.canManage(actorJid, data.getBundleJid().get()));
        }

        contest = contestStore.updateContest(contestJid, data);

        contestLogger.log(contestJid, "UPDATE_CONTEST");

        return contest;
    }

    @GET
    @Path("/slug/{contestSlug}")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public Contest getContestBySlug(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @PathParam("contestSlug") String contestSlug) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestBySlug(contestSlug));

        checkAllowed(contestRoleChecker.canView(actorJid, contest));
        return contest;
    }

    @GET
    @Path("/bundle/{bundleSlug}")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ContestsResponse getContestByBundleSlug(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @PathParam("bundleSlug") String bundleSlug,
            @QueryParam("page") @DefaultValue("1") int pageNumber) {

        String actorJid = actorChecker.check(authHeader);
        boolean isAdmin = contestRoleChecker.canAdminister(actorJid);

        ContestBundle bundle = checkFound(contestBundleStore.getContestBundleBySlug(bundleSlug));
        Optional<String> userJid = isAdmin ? Optional.empty() : Optional.of(actorJid);

        Page<Contest> contests = contestStore.getAllContestsByBundle(bundle.getJid(), userJid, pageNumber, BUNDLE_PAGE_SIZE);

        Map<String, ContestRole> rolesMap = contests
                .getPage()
                .stream()
                .collect(Collectors.toMap(
                        Contest::getJid,
                        contest -> contestRoleChecker.getRole(actorJid, contest)));

        boolean canAdminister = contestRoleChecker.canAdminister(actorJid);
        ContestConfig config = new ContestConfig.Builder()
                .canAdminister(canAdminister)
                .build();

        return new ContestsResponse.Builder()
                .data(contests)
                .rolesMap(rolesMap)
                .config(config)
                .build();
    }

    @POST
    @Path("/{contestJid}/virtual")
    @UnitOfWork
    public void startVirtualContest(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("contestJid") String contestJid) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(contestRoleChecker.canStartVirtual(actorJid, contest));

        contestantStore.startVirtualContest(contestJid, actorJid);

        contestLogger.log(contestJid, "START_VIRTUAL_CONTEST");
    }

    @PUT
    @Path("/{contestJid}/virtual/reset")
    @UnitOfWork
    public void resetVirtualContest(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("contestJid") String contestJid) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(contestRoleChecker.canResetVirtual(actorJid, contest));

        contestantStore.resetVirtualContest(contestJid);

        contestLogger.log(contestJid, "RESET_VIRTUAL_CONTEST");
    }

    @GET
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ContestsResponse getContests(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @QueryParam("name") Optional<String> name,
            @QueryParam("page") @DefaultValue("1") int pageNumber) {

        String actorJid = actorChecker.check(authHeader);
        boolean isAdmin = contestRoleChecker.canAdminister(actorJid);

        Optional<String> userJid = isAdmin ? Optional.empty() : Optional.of(actorJid);
        Page<Contest> contests = contestStore.getContests(userJid, name, pageNumber, PAGE_SIZE);

        Map<String, ContestRole> rolesMap = contests.getPage()
                .stream()
                .collect(Collectors.toMap(
                        Contest::getJid,
                        contest -> contestRoleChecker.getRole(actorJid, contest)));

        boolean canAdminister = contestRoleChecker.canAdminister(actorJid);
        ContestConfig config = new ContestConfig.Builder()
                .canAdminister(canAdminister)
                .build();

        return new ContestsResponse.Builder()
                .data(contests)
                .rolesMap(rolesMap)
                .config(config)
                .build();
    }

    @GET
    @Path("/active")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ActiveContestsResponse getActiveContests(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader) {

        String actorJid = actorChecker.check(authHeader);
        boolean isAdmin = contestRoleChecker.canAdminister(actorJid);

        Optional<String> userJid = isAdmin ? Optional.empty() : Optional.of(actorJid);
        List<Contest> contests = contestStore.getActiveContests(userJid);

        Map<String, ContestRole> rolesMap = contests
                .stream()
                .collect(Collectors.toMap(
                        Contest::getJid,
                        contest -> contestRoleChecker.getRole(actorJid, contest)));

        return new ActiveContestsResponse.Builder()
                .data(contests)
                .rolesMap(rolesMap)
                .build();
    }

    @POST
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @UnitOfWork
    public Contest createContest(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            ContestCreateData data) {

        String actorJid = actorChecker.check(authHeader);

        if (data.getBundleJid().isPresent()) {
            Optional<ContestBundle> contestBundle = contestBundleStore.getContestBundleByJid(data.getBundleJid().get());
            if (!contestBundle.isPresent()) {
                throw ContestErrors.bundleDoesNotExist(data.getBundleJid().get());
            }

            checkAllowed(contestBundleRoleChecker.canManage(actorJid, data.getBundleJid().get()));
        } else {
            checkAllowed(contestRoleChecker.canAdminister(actorJid));
        }

        Contest contest = contestStore.createContest(data);
        moduleStore.upsertIcpcStyleModule(contest.getJid(), new IcpcStyleModuleConfig.Builder().build());
        if (data.getBundleJid().isPresent()) {
            String bundleJid = data.getBundleJid().get();
            if (data.getIsInsertDefaultSupervisor().orElse(false) && data.getSupervisorPermissions().isPresent()) {
                for (String supervisorJid : contestBundleSupervisorStore.getSupervisors(bundleJid, 1, MAX_BOUND_AUTO_INSERT).getPage()
                        .stream()
                        .map(s -> s.getUserJid())
                        .collect(Collectors.toList())) {
                    contestSupervisorStore.upsertSupervisor(contest.getJid(), supervisorJid, data.getSupervisorPermissions().get());
                }
            }
            if (data.getIsInsertDefaultContestant().orElse(false)) {
                for (String contestantJid : contestBundleContestantStore.getContestants(bundleJid, 1, MAX_BOUND_AUTO_INSERT).getPage()
                        .stream()
                        .map(c -> c.getUserJid())
                        .collect(Collectors.toList())) {
                    contestContestantStore.upsertContestant(contest.getJid(), contestantJid);
                }
            }
        }

        contestLogger.log(contest.getJid(), "CREATE_CONTEST");

        return contest;
    }

    @GET
    @Path("/{contestJid}/description")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ContestDescription getContestDescription(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @PathParam("contestJid") String contestJid) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(contestRoleChecker.canView(actorJid, contest));

        contestLogger.log(contest.getJid(), "OPEN_CONTEST");

        String description = contestStore.getContestDescription(contest.getJid());
        return new ContestDescription.Builder()
                .description(description)
                .profilesMap(jophielClient.parseProfiles(description))
                .build();
    }

    @POST
    @Path("/{contestJid}/description")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @UnitOfWork
    public ContestDescription updateContestDescription(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("contestJid") String contestJid,
            ContestDescription description) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(contestRoleChecker.canManage(actorJid, contest));

        ContestDescription newDescription = contestStore.updateContestDescription(contest.getJid(), description);

        contestLogger.log(contest.getJid(), "UPDATE_DESCRIPTION");

        return newDescription;
    }
}
