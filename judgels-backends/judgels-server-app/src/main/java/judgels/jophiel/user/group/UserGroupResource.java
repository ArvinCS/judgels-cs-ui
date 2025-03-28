package judgels.jophiel.user.group;

import static com.google.common.base.Preconditions.checkArgument;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.dropwizard.hibernate.UnitOfWork;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
import judgels.jerahmeel.role.RoleChecker;
import judgels.jophiel.JophielClient;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.group.UserGroup;
import judgels.jophiel.api.user.group.UsersGroupDeleteData;
import judgels.jophiel.api.user.group.UsersGroupDeleteResponse;
import judgels.jophiel.api.user.group.UsersGroupResponse;
import judgels.jophiel.api.user.group.UsersGroupUpsertData;
import judgels.jophiel.api.user.group.UsersGroupUpsertResponse;
import judgels.jophiel.user.UserStore;
import judgels.persistence.api.OrderDir;
import judgels.persistence.api.Page;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;

@Path("/api/v2/users")
public class UserGroupResource {
    private static final int PAGE_SIZE = 20;

    @Inject protected ActorChecker actorChecker;
    @Inject protected RoleChecker roleChecker;
    @Inject protected UserStore userStore;
    @Inject protected UserGroupStore userGroupStore;
    @Inject protected JophielClient jophielClient;

    @Inject public UserGroupResource() {}

    @GET
    @Path("/group")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public UsersGroupResponse getGroup(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @QueryParam("name") Optional<String> name,
            @QueryParam("orderBy") Optional<String> orderBy,
            @QueryParam("orderDir") Optional<OrderDir> orderDir,
            @QueryParam("page") @DefaultValue("1") int pageNumber) {

        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.isAdmin(actorJid));

        Page<User> users = userStore.getUsers(pageNumber, PAGE_SIZE, name, orderBy, orderDir);

        var userJids = Lists.transform(users.getPage(), User::getJid);
        Map<String, List<String>> groupsMap = userGroupStore.getGroupByUserJids(userJids);
        return new UsersGroupResponse.Builder()
                .data(users)
                .groupsMap(groupsMap)
                .build();
    }

    @POST
    @Path("/group/batch-upsert")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @UnitOfWork
    public UsersGroupUpsertResponse batchUpsert(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            UsersGroupUpsertData data) {

        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.isAdmin(actorJid));

        checkArgument(data.getUsernames().size() <= 100, "Cannot add more than 100 users.");
        checkArgument(data.getGroups().size() <= 10, "Cannot add more than 10 groups.");

        Map<String, String> usernameToJidMap = jophielClient.translateUsernamesToJids(data.getUsernames());

        Map<String, Set<String>> insertedGroupUsernamesMap = new HashMap<>();
        Map<String, Set<String>> alreadyGroupUsernamesMap = new HashMap<>();
        usernameToJidMap.forEach((username, userJid) -> {
            UserGroup group = userGroupStore.getGroup(userJid);

            data.getGroups().forEach(candidate -> {
                if (group.getGroups().isPresent() && group.getGroups().get().contains(candidate)) {
                    alreadyGroupUsernamesMap.computeIfAbsent(username, k -> Sets.newHashSet()).add(username);
                } else {
                    insertedGroupUsernamesMap.computeIfAbsent(username, k -> Sets.newHashSet()).add(username);
                }
            });

            Set<String> updatedGroups = new HashSet<>(group.getGroups().orElse(Collections.emptyList()));
            updatedGroups.addAll(data.getGroups());
            UserGroup updatedGroup = new UserGroup.Builder()
                    .from(group)
                    .groups(List.copyOf(updatedGroups))
                    .build();
            userGroupStore.upsertGroup(userJid, updatedGroup);
        });

        return new UsersGroupUpsertResponse.Builder()
                .insertedGroupUsernamesMap(insertedGroupUsernamesMap)
                .alreadyGroupUsernamesMap(alreadyGroupUsernamesMap)
                .build();
    }

    @POST
    @Path("/group/batch-delete")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @UnitOfWork
    public UsersGroupDeleteResponse batchDelete(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            UsersGroupDeleteData data) {

        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.isAdmin(actorJid));

        checkArgument(data.getUsernames().size() <= 100, "Cannot remove more than 100 users.");
        checkArgument(data.getGroups().size() <= 10, "Cannot remove more than 10 groups.");

        Map<String, String> usernameToJidMap = jophielClient.translateUsernamesToJids(data.getUsernames());

        Map<String, Set<String>> deletedGroupUsernamesMap = new HashMap<>();
        Map<String, Set<String>> notGroupUsernamesMap = new HashMap<>();
        usernameToJidMap.forEach((username, userJid) -> {
            UserGroup group = userGroupStore.getGroup(userJid);

            data.getGroups().forEach(candidate -> {
                if (group.getGroups().isPresent() && group.getGroups().get().contains(candidate)) {
                    deletedGroupUsernamesMap.computeIfAbsent(username, k -> Sets.newHashSet()).add(username);
                } else {
                    notGroupUsernamesMap.computeIfAbsent(username, k -> Sets.newHashSet()).add(username);
                }
            });

            Set<String> updatedGroups = new HashSet<>(group.getGroups().orElse(Collections.emptyList()));
            updatedGroups.removeAll(data.getGroups());
            UserGroup updatedGroup = new UserGroup.Builder()
                    .from(group)
                    .groups(List.copyOf(updatedGroups))
                    .build();
            userGroupStore.upsertGroup(userJid, updatedGroup);
        });

        return new UsersGroupDeleteResponse.Builder()
                .deletedGroupUsernamesMap(deletedGroupUsernamesMap)
                .notGroupUsernamesMap(notGroupUsernamesMap)
                .build();
    }

    @GET
    @Path("/{userJid}/group")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public UserGroup getGroups(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("userJid") String userJid) {

        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.isAdmin(actorJid));

        User user = checkFound(userStore.getUserByJid(userJid));
        return userGroupStore.getGroup(user.getJid());
    }

    @PUT
    @Path("/{userJid}/group")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @UnitOfWork
    public UserGroup updateGroup(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("userJid") String userJid,
            UserGroup userGroup) {
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.isAdmin(actorJid));

        User user = checkFound(userStore.getUserByJid(userJid));
        return userGroupStore.upsertGroup(user.getJid(), userGroup);
    }
}
