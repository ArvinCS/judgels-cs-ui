package judgels.uriel.bundle.supervisor;

import static com.google.common.base.Preconditions.checkArgument;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.Lists;
import io.dropwizard.hibernate.UnitOfWork;
import java.util.Map;
import java.util.Set;
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
import judgels.jophiel.JophielClient;
import judgels.jophiel.api.profile.Profile;
import judgels.persistence.api.Page;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.bundle.ContestBundle;
import judgels.uriel.api.bundle.supervisor.ContestBundleSupervisor;
import judgels.uriel.api.bundle.supervisor.ContestBundleSupervisorsDeleteResponse;
import judgels.uriel.api.bundle.supervisor.ContestBundleSupervisorsResponse;
import judgels.uriel.api.bundle.supervisor.ContestBundleSupervisorsUpsertResponse;
import judgels.uriel.bundle.ContestBundleStore;
import org.glassfish.jersey.internal.guava.Sets;

@Path("/api/v2/contest-bundles/{bundleJid}/supervisors")
public class ContestBundleSupervisorResource {
    private static final int PAGE_SIZE = 10;

    @Inject
    protected ActorChecker actorChecker;
    @Inject
    protected ContestBundleStore contestBundleStore;
    @Inject
    protected ContestBundleSupervisorRoleChecker supervisorRoleChecker;
    @Inject
    protected ContestBundleSupervisorStore supervisorStore;
    @Inject
    protected JophielClient jophielClient;

    @Inject
    public ContestBundleSupervisorResource() {
    }

    @GET
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ContestBundleSupervisorsResponse getSupervisors(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("bundleJid") String bundleJid,
            @QueryParam("page") @DefaultValue("1") int pageNumber) {
        String actorJid = actorChecker.check(authHeader);
        ContestBundle bundle = checkFound(contestBundleStore.getContestBundleByJid(bundleJid));
        checkAllowed(supervisorRoleChecker.canView(actorJid, bundle));

        Page<ContestBundleSupervisor> supervisors = supervisorStore.getSupervisors(bundleJid, pageNumber, PAGE_SIZE);

        var userJids = Lists.transform(supervisors.getPage(), ContestBundleSupervisor::getUserJid);
        Map<String, Profile> profilesMap = jophielClient.getProfiles(userJids);

        return new ContestBundleSupervisorsResponse.Builder()
                .data(supervisors)
                .profilesMap(profilesMap)
                .build();
    }

    @POST
    @Path("/batch-upsert")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @UnitOfWork
    public ContestBundleSupervisorsUpsertResponse batchUpsert(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("bundleJid") String bundleJid,
            Set<String> usernames) {

        String actorJid = actorChecker.check(authHeader);
        ContestBundle bundle = checkFound(contestBundleStore.getContestBundleByJid(bundleJid));
        checkAllowed(supervisorRoleChecker.canManage(actorJid, bundle));

        checkArgument(usernames.size() <= 100, "Cannot add more than 100 users.");

        Map<String, String> usernameToJidMap = jophielClient.translateUsernamesToJids(usernames);

        Set<String> insertedSupervisorUsernames = Sets.newHashSet();
        Set<String> alreadySupervisorUsernames = Sets.newHashSet();
        usernameToJidMap.forEach((username, userJid) -> {
            if (supervisorStore.upsertSupervisor(bundle.getJid(), userJid)) {
                insertedSupervisorUsernames.add(username);
            } else {
                alreadySupervisorUsernames.add(username);
            }
        });

        Map<String, Profile> userJidToProfileMap = jophielClient.getProfiles(usernameToJidMap.values());
        Map<String, Profile> insertedSupervisorProfilesMap = insertedSupervisorUsernames
                .stream()
                .collect(Collectors.toMap(u -> u, u -> userJidToProfileMap.get(usernameToJidMap.get(u))));
        Map<String, Profile> alreadySupervisorProfilesMap = alreadySupervisorUsernames
                .stream()
                .collect(Collectors.toMap(u -> u, u -> userJidToProfileMap.get(usernameToJidMap.get(u))));

        return new ContestBundleSupervisorsUpsertResponse.Builder()
                .insertedManagerProfilesMap(insertedSupervisorProfilesMap)
                .alreadyManagerProfilesMap(alreadySupervisorProfilesMap)
                .build();
    }

    @POST
    @Path("/batch-delete")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @UnitOfWork
    public ContestBundleSupervisorsDeleteResponse deleteSupervisors(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("bundleJid") String bundleJid,
            Set<String> usernames) {

        String actorJid = actorChecker.check(authHeader);
        ContestBundle bundle = checkFound(contestBundleStore.getContestBundleByJid(bundleJid));
        checkAllowed(supervisorRoleChecker.canManage(actorJid, bundle));

        checkArgument(usernames.size() <= 100, "Cannot add more than 100 users.");

        Map<String, String> usernameToJidMap = jophielClient.translateUsernamesToJids(usernames);

        Set<String> deletedSupervisorUsernames = Sets.newHashSet();
        Set<String> notSupervisorUsernames = Sets.newHashSet();
        usernameToJidMap.forEach((username, userJid) -> {
            if (supervisorStore.deleteSupervisor(bundle.getJid(), userJid)) {
                deletedSupervisorUsernames.add(username);
            } else {
                notSupervisorUsernames.add(username);
            }
        });

        Map<String, Profile> userJidToProfileMap = jophielClient.getProfiles(usernameToJidMap.values());
        Map<String, Profile> deletedSupervisorProfilesMap = deletedSupervisorUsernames
                .stream()
                .collect(Collectors.toMap(u -> u, u -> userJidToProfileMap.get(usernameToJidMap.get(u))));
        Map<String, Profile> notSupervisorProfilesMap = notSupervisorUsernames
                .stream()
                .collect(Collectors.toMap(u -> u, u -> userJidToProfileMap.get(usernameToJidMap.get(u))));

        return new ContestBundleSupervisorsDeleteResponse.Builder()
                .deletedManagerProfilesMap(deletedSupervisorProfilesMap)
                .notManagerProfilesMap(notSupervisorProfilesMap)
                .build();
    }
}
