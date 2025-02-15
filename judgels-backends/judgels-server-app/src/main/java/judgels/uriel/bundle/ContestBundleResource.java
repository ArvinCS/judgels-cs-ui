package judgels.uriel.bundle;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import io.dropwizard.hibernate.UnitOfWork;
import judgels.jophiel.JophielClient;
import judgels.persistence.api.Page;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.bundle.ContestBundle;
import judgels.uriel.api.bundle.ContestBundleConfig;
import judgels.uriel.api.bundle.ContestBundleCreateData;
import judgels.uriel.api.bundle.ContestBundleUpdateData;
import judgels.uriel.api.bundle.ContestBundlesResponse;
import judgels.uriel.api.bundle.role.ContestBundleRole;
import judgels.uriel.contest.ContestStore;

@Path("/api/v2/contest-bundles")
public class ContestBundleResource {
    private static final int PAGE_SIZE = 6;

    @Inject protected ActorChecker actorChecker;
    @Inject protected ContestBundleRoleChecker contestBundleRoleChecker;
    @Inject protected ContestBundleStore contestBundleStore;
    @Inject protected ContestStore contestStore;
    @Inject protected JophielClient jophielClient;

    @Inject public ContestBundleResource() {}

    @GET
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ContestBundlesResponse getContestBundles(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @QueryParam("name") Optional<String> name,
            @QueryParam("page") @DefaultValue("1") int pageNumber) {

        String actorJid = actorChecker.check(authHeader);
        boolean isAdmin = contestBundleRoleChecker.canAdminister(actorJid);

        Optional<String> userJid = isAdmin ? Optional.empty() : Optional.of(actorJid);
        Page<ContestBundle> contestBundles = contestBundleStore.getContestBundles(userJid, name, pageNumber, PAGE_SIZE);

        Map<String, ContestBundleRole> rolesMap = contestBundles.getPage()
                .stream()
                .collect(Collectors.toMap(
                        ContestBundle::getJid,
                        contest -> contestBundleRoleChecker.getRole(actorJid, contest)));

        boolean canAdminister = contestBundleRoleChecker.canAdminister(actorJid);
        ContestBundleConfig config = new ContestBundleConfig.Builder()
                .canAdminister(canAdminister)
                .build();

        return new ContestBundlesResponse.Builder()
                .data(contestBundles)
                .rolesMap(rolesMap)
                .config(config)
                .build();
    }

    @POST
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @UnitOfWork
    public ContestBundle createContestBundle(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            ContestBundleCreateData data) {

        String actorJid = actorChecker.check(authHeader);
        checkAllowed(contestBundleRoleChecker.canAdminister(actorJid));

        ContestBundle bundle = contestBundleStore.createContestBundle(data);

        return bundle;
    }

    @GET
    @Path("/{bundleJid}")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ContestBundle getContestBundle(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @PathParam("bundleJid") String bundleJid) {

        String actorJid = actorChecker.check(authHeader);
        ContestBundle bundle = checkFound(contestBundleStore.getContestBundleByJid(bundleJid));

        checkAllowed(contestBundleRoleChecker.canView(actorJid, bundle));
        return bundle;
    }

    @POST
    @Path("/{bundleJid}")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @UnitOfWork
    public ContestBundle updateContestBundle(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @PathParam("bundleJid") String bundleJid,
            ContestBundleUpdateData data) {

        String actorJid = actorChecker.check(authHeader);
        ContestBundle bundle = checkFound(contestBundleStore.getContestBundleByJid(bundleJid));

        checkAllowed(contestBundleRoleChecker.canManage(actorJid, bundle));
        bundle = contestBundleStore.updateContestBundle(bundleJid, data);

        return bundle;
    }

    @GET
    @Path("/slug/{contestBundleSlug}")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ContestBundle getContestBundleBySlug(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @PathParam("contestBundleSlug") String contestBundleSlug) {

        String actorJid = actorChecker.check(authHeader);
        ContestBundle bundle = checkFound(contestBundleStore.getContestBundleBySlug(contestBundleSlug));

        checkAllowed(contestBundleRoleChecker.canView(actorJid, bundle));
        return bundle;
    }
}
