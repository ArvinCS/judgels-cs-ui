package judgels.uriel.bundle.manager;

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
import judgels.uriel.api.bundle.manager.ContestBundleManager;
import judgels.uriel.api.bundle.manager.ContestBundleManagerConfig;
import judgels.uriel.api.bundle.manager.ContestBundleManagersDeleteResponse;
import judgels.uriel.api.bundle.manager.ContestBundleManagersResponse;
import judgels.uriel.api.bundle.manager.ContestBundleManagersUpsertResponse;
import judgels.uriel.bundle.ContestBundleStore;
import org.glassfish.jersey.internal.guava.Sets;

@Path("/api/v2/contest-bundles/{bundleJid}/managers")
public class ContestBundleManagerResource {
    private static final int PAGE_SIZE = 20;

    @Inject protected ActorChecker actorChecker;
    @Inject protected ContestBundleStore contestBundleStore;
    @Inject protected ContestBundleManagerRoleChecker managerRoleChecker;
    @Inject protected ContestBundleManagerStore managerStore;
    @Inject protected JophielClient jophielClient;

    @Inject public ContestBundleManagerResource() {}

    @GET
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ContestBundleManagersResponse getManagers(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("bundleJid") String bundleJid,
            @QueryParam("page") @DefaultValue("1") int pageNumber) {

        String actorJid = actorChecker.check(authHeader);
        ContestBundle bundle = checkFound(contestBundleStore.getContestBundleByJid(bundleJid));
        checkAllowed(managerRoleChecker.canView(actorJid, bundle));

        Page<ContestBundleManager> managers = managerStore.getManagers(bundleJid, pageNumber, PAGE_SIZE);

        var userJids = Lists.transform(managers.getPage(), ContestBundleManager::getUserJid);
        Map<String, Profile> profilesMap = jophielClient.getProfiles(userJids);

        boolean canManage = managerRoleChecker.canManage(actorJid);
        ContestBundleManagerConfig config = new ContestBundleManagerConfig.Builder()
                .canManage(canManage)
                .build();

        return new ContestBundleManagersResponse.Builder()
                .data(managers)
                .profilesMap(profilesMap)
                .config(config)
                .build();
    }

    @POST
    @Path("/batch-upsert")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @UnitOfWork
    public ContestBundleManagersUpsertResponse batchUpsert(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("bundleJid") String bundleJid,
            Set<String> usernames) {

        String actorJid = actorChecker.check(authHeader);
        ContestBundle bundle = checkFound(contestBundleStore.getContestBundleByJid(bundleJid));
        checkAllowed(managerRoleChecker.canManage(actorJid));

        checkArgument(usernames.size() <= 100, "Cannot add more than 100 users.");

        Map<String, String> usernameToJidMap = jophielClient.translateUsernamesToJids(usernames);

        Set<String> insertedManagerUsernames = Sets.newHashSet();
        Set<String> alreadyManagerUsernames = Sets.newHashSet();
        usernameToJidMap.forEach((username, userJid) -> {
            if (managerStore.upsertManager(bundle.getJid(), userJid)) {
                insertedManagerUsernames.add(username);
            } else {
                alreadyManagerUsernames.add(username);
            }
        });

        Map<String, Profile> userJidToProfileMap = jophielClient.getProfiles(usernameToJidMap.values());
        Map<String, Profile> insertedManagerProfilesMap = insertedManagerUsernames
                .stream()
                .collect(Collectors.toMap(u -> u, u -> userJidToProfileMap.get(usernameToJidMap.get(u))));
        Map<String, Profile> alreadyManagerProfilesMap = alreadyManagerUsernames
                .stream()
                .collect(Collectors.toMap(u -> u, u -> userJidToProfileMap.get(usernameToJidMap.get(u))));

        return new ContestBundleManagersUpsertResponse.Builder()
                .insertedManagerProfilesMap(insertedManagerProfilesMap)
                .alreadyManagerProfilesMap(alreadyManagerProfilesMap)
                .build();
    }

    @POST
    @Path("/batch-delete")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @UnitOfWork
    public ContestBundleManagersDeleteResponse deleteManagers(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("bundleJid") String bundleJid,
            Set<String> usernames) {

        String actorJid = actorChecker.check(authHeader);
        ContestBundle bundle = checkFound(contestBundleStore.getContestBundleByJid(bundleJid));
        checkAllowed(managerRoleChecker.canManage(actorJid));

        checkArgument(usernames.size() <= 100, "Cannot delete more than 100 users.");

        Map<String, String> usernameToJidMap = jophielClient.translateUsernamesToJids(usernames);

        Set<String> deletedManagerUsernames = Sets.newHashSet();
        Set<String> notManagerUsernames = Sets.newHashSet();
        usernameToJidMap.forEach((username, userJid) -> {
            if (managerStore.deleteManager(bundle.getJid(), userJid)) {
                deletedManagerUsernames.add(username);
            } else {
                notManagerUsernames.add(username);
            }
        });

        Map<String, Profile> userJidToProfileMap = jophielClient.getProfiles(usernameToJidMap.values());
        Map<String, Profile> deletedManagerProfilesMap = deletedManagerUsernames
                .stream()
                .collect(Collectors.toMap(u -> u, u -> userJidToProfileMap.get(usernameToJidMap.get(u))));
        Map<String, Profile> notManagerProfilesMap = notManagerUsernames
                .stream()
                .collect(Collectors.toMap(u -> u, u -> userJidToProfileMap.get(usernameToJidMap.get(u))));

        return new ContestBundleManagersDeleteResponse.Builder()
                .deletedManagerProfilesMap(deletedManagerProfilesMap)
                .notManagerProfilesMap(notManagerProfilesMap)
                .build();
    }
}
