package judgels.uriel.bundle.contestant;

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
import judgels.uriel.api.bundle.contestant.ContestBundleContestant;
import judgels.uriel.api.bundle.contestant.ContestBundleContestantsDeleteResponse;
import judgels.uriel.api.bundle.contestant.ContestBundleContestantsResponse;
import judgels.uriel.api.bundle.contestant.ContestBundleContestantsUpsertResponse;
import judgels.uriel.bundle.ContestBundleStore;
import org.glassfish.jersey.internal.guava.Sets;

@Path("/api/v2/contest-bundles/{bundleJid}/contestants")
public class ContestBundleContestantResource {
    private static final int PAGE_SIZE = 10;

    @Inject
    protected ActorChecker actorChecker;
    @Inject
    protected ContestBundleStore contestBundleStore;
    @Inject
    protected ContestBundleContestantRoleChecker contestantRoleChecker;
    @Inject
    protected ContestBundleContestantStore contestantStore;
    @Inject
    protected JophielClient jophielClient;

    @Inject
    public ContestBundleContestantResource() {
    }

    @GET
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ContestBundleContestantsResponse getContestants(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("bundleJid") String bundleJid,
            @QueryParam("page") @DefaultValue("1") int pageNumber) {
        String actorJid = actorChecker.check(authHeader);
        ContestBundle bundle = checkFound(contestBundleStore.getContestBundleByJid(bundleJid));
        checkAllowed(contestantRoleChecker.canView(actorJid, bundle));

        Page<ContestBundleContestant> contestants = contestantStore.getContestants(bundleJid, pageNumber, PAGE_SIZE);

        var userJids = Lists.transform(contestants.getPage(), ContestBundleContestant::getUserJid);
        Map<String, Profile> profilesMap = jophielClient.getProfiles(userJids);

        return new ContestBundleContestantsResponse.Builder()
                .data(contestants)
                .profilesMap(profilesMap)
                .build();
    }

    @POST
    @Path("/batch-upsert")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @UnitOfWork
    public ContestBundleContestantsUpsertResponse batchUpsert(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("bundleJid") String bundleJid,
            Set<String> usernames) {

        String actorJid = actorChecker.check(authHeader);
        ContestBundle bundle = checkFound(contestBundleStore.getContestBundleByJid(bundleJid));
        checkAllowed(contestantRoleChecker.canManage(actorJid, bundle));

        checkArgument(usernames.size() <= 100, "Cannot add more than 100 users.");

        Map<String, String> usernameToJidMap = jophielClient.translateUsernamesToJids(usernames);

        Set<String> insertedContestantUsernames = Sets.newHashSet();
        Set<String> alreadyContestantUsernames = Sets.newHashSet();
        usernameToJidMap.forEach((username, userJid) -> {
            if (contestantStore.upsertContestant(bundle.getJid(), userJid)) {
                insertedContestantUsernames.add(username);
            } else {
                alreadyContestantUsernames.add(username);
            }
        });

        Map<String, Profile> userJidToProfileMap = jophielClient.getProfiles(usernameToJidMap.values());
        Map<String, Profile> insertedSupervisorProfilesMap = insertedContestantUsernames
                .stream()
                .collect(Collectors.toMap(u -> u, u -> userJidToProfileMap.get(usernameToJidMap.get(u))));
        Map<String, Profile> alreadySupervisorProfilesMap = alreadyContestantUsernames
                .stream()
                .collect(Collectors.toMap(u -> u, u -> userJidToProfileMap.get(usernameToJidMap.get(u))));

        return new ContestBundleContestantsUpsertResponse.Builder()
                .insertedManagerProfilesMap(insertedSupervisorProfilesMap)
                .alreadyManagerProfilesMap(alreadySupervisorProfilesMap)
                .build();
    }

    @POST
    @Path("/batch-delete")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @UnitOfWork
    public ContestBundleContestantsDeleteResponse deleteContestants(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("bundleJid") String bundleJid,
            Set<String> usernames) {

        String actorJid = actorChecker.check(authHeader);
        ContestBundle bundle = checkFound(contestBundleStore.getContestBundleByJid(bundleJid));
        checkAllowed(contestantRoleChecker.canManage(actorJid, bundle));

        checkArgument(usernames.size() <= 100, "Cannot add more than 100 users.");

        Map<String, String> usernameToJidMap = jophielClient.translateUsernamesToJids(usernames);

        Set<String> deletedContestantUsernames = Sets.newHashSet();
        Set<String> notContestantUsernames = Sets.newHashSet();
        usernameToJidMap.forEach((username, userJid) -> {
            if (contestantStore.deleteContestant(bundle.getJid(), userJid)) {
                deletedContestantUsernames.add(username);
            } else {
                notContestantUsernames.add(username);
            }
        });

        Map<String, Profile> userJidToProfileMap = jophielClient.getProfiles(usernameToJidMap.values());
        Map<String, Profile> deletedSupervisorProfilesMap = deletedContestantUsernames
                .stream()
                .collect(Collectors.toMap(u -> u, u -> userJidToProfileMap.get(usernameToJidMap.get(u))));
        Map<String, Profile> notSupervisorProfilesMap = notContestantUsernames
                .stream()
                .collect(Collectors.toMap(u -> u, u -> userJidToProfileMap.get(usernameToJidMap.get(u))));

        return new ContestBundleContestantsDeleteResponse.Builder()
                .deletedManagerProfilesMap(deletedSupervisorProfilesMap)
                .notManagerProfilesMap(notSupervisorProfilesMap)
                .build();
    }
}
