package judgels.michael.problem.automaton.grading;

import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.views.View;
import java.io.InputStream;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import judgels.fs.FileInfo;
import judgels.gabriel.api.AutomatonRestriction;
import judgels.gabriel.api.GradingConfig;
import judgels.gabriel.engines.GradingEngineRegistry;
import judgels.jophiel.api.actor.Actor;
import judgels.michael.problem.automaton.BaseAutomatonProblemResource;
import judgels.michael.problem.programming.grading.config.EditGradingConfigView;
import judgels.michael.problem.programming.grading.config.GradingConfigAdapter;
import judgels.michael.problem.programming.grading.config.GradingConfigAdapterRegistry;
import judgels.michael.problem.programming.grading.config.GradingConfigForm;
import judgels.michael.resource.ListFilesView;
import judgels.michael.template.HtmlTemplate;
import judgels.sandalphon.api.problem.Problem;
import judgels.service.ServiceUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

@Path("/problems/automaton/{problemId}/grading")
public class AutomatonProblemGradingResource extends BaseAutomatonProblemResource {
    @Inject public AutomatonProblemGradingResource() {}

    @GET
    @Path("/engine")
    @UnitOfWork(readOnly = true)
    public View editGradingEngine(@Context HttpServletRequest req, @PathParam("problemId") int problemId) {
        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canView(actor, problem));

        EditGradingEngineForm form = new EditGradingEngineForm();
        form.gradingEngine = automatonProblemStore.getGradingEngine(actor.getUserJid(), problem.getJid());

        return renderEditGradingEngine(actor, problem, form, roleChecker.canEdit(actor, problem));
    }

    private View renderEditGradingEngine(Actor actor, Problem problem, EditGradingEngineForm form, boolean canEdit) {
        HtmlTemplate template = newProblemGradingTemplate(actor, problem);
        template.setActiveSecondaryTab("engine");
        return new EditGradingEngineView(template, form, canEdit);
    }

    @POST
    @Path("/engine")
    @UnitOfWork
    public Response updateGradingEngine(
            @Context HttpServletRequest req,
            @PathParam("problemId") int problemId,
            @BeanParam EditGradingEngineForm form) {

        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canEdit(actor, problem));

        problemStore.createUserCloneIfNotExists(actor.getUserJid(), problem.getJid());

        String engine = form.gradingEngine;
        String oldEngine = automatonProblemStore.getGradingEngine(actor.getUserJid(), problem.getJid());

        if (!engine.equals(oldEngine)) {
            GradingConfig config = GradingEngineRegistry.getInstance().get(engine).createDefaultConfig();
            automatonProblemStore.updateGradingConfig(actor.getUserJid(), problem.getJid(), config);
        }

        automatonProblemStore.updateGradingEngine(actor.getUserJid(), problem.getJid(), engine);

        return redirect("/problems/automaton/" + problemId + "/grading/config");
    }

    @GET
    @Path("/config")
    @UnitOfWork(readOnly = true)
    public View editGradingConfig(@Context HttpServletRequest req, @PathParam("problemId") int problemId) {
        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canView(actor, problem));

        String engine = automatonProblemStore.getGradingEngine(actor.getUserJid(), problem.getJid());
        GradingConfig config  = automatonProblemStore.getGradingConfig(actor.getUserJid(), problem.getJid());
        List<FileInfo> testDataFiles = automatonProblemStore.getGradingTestDataFiles(actor.getUserJid(), problem.getJid());
        List<FileInfo> helperFiles = automatonProblemStore.getGradingHelperFiles(actor.getUserJid(), problem.getJid());

        GradingConfigAdapter adapter = GradingConfigAdapterRegistry.getInstance().get(engine);
        GradingConfigForm form = adapter.buildFormFromConfig(config);

        HtmlTemplate template = newProblemGradingTemplate(actor, problem);
        template.setActiveSecondaryTab("config");
        return new EditGradingConfigView(engine, template, form, testDataFiles, helperFiles, roleChecker.canEdit(actor, problem));
    }

    @POST
    @Path("/config")
    @UnitOfWork
    public Response updateGradingConfig(
            @Context HttpServletRequest req,
            @PathParam("problemId") int problemId,
            @BeanParam GradingConfigForm form) {

        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canEdit(actor, problem));

        String engine = automatonProblemStore.getGradingEngine(actor.getUserJid(), problem.getJid());

        problemStore.createUserCloneIfNotExists(actor.getUserJid(), problem.getJid());

        GradingConfigAdapter adapter = GradingConfigAdapterRegistry.getInstance().get(engine);
        GradingConfig config = adapter.buildConfigFromForm(form);

        automatonProblemStore.updateGradingConfig(actor.getUserJid(), problem.getJid(), config);

        return redirect("/problems/automaton/" + problemId + "/grading/config");
    }

    @GET
    @Path("/config/auto-populate")
    @UnitOfWork
    public Response autoPopulateGradingConfigTestData(@Context HttpServletRequest req, @PathParam("problemId") int problemId) {
        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canEdit(actor, problem));

        String engine = automatonProblemStore.getGradingEngine(actor.getUserJid(), problem.getJid());
        GradingConfig config  = automatonProblemStore.getGradingConfig(actor.getUserJid(), problem.getJid());
        List<FileInfo> testDataFiles = automatonProblemStore.getGradingTestDataFiles(actor.getUserJid(), problem.getJid());

        problemStore.createUserCloneIfNotExists(actor.getUserJid(), problem.getJid());

        GradingConfigAdapter adapter = GradingConfigAdapterRegistry.getInstance().get(engine);
        GradingConfig newConfig = adapter.autoPopulateTestData(config, testDataFiles);

        automatonProblemStore.updateGradingConfig(actor.getUserJid(), problem.getJid(), newConfig);

        return redirect("/problems/automaton/" + problemId + "/grading/config");
    }

    @GET
    @Path("/testdata")
    @UnitOfWork(readOnly = true)
    public View listGradingTestDataFiles(@Context HttpServletRequest req, @PathParam("problemId") int problemId) {
        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canView(actor, problem));

        List<FileInfo> testDataFiles = automatonProblemStore.getGradingTestDataFiles(actor.getUserJid(), problem.getJid());

        HtmlTemplate template = newProblemGradingTemplate(actor, problem);
        template.setActiveSecondaryTab("testdata");
        return new ListFilesView(template, req.getRequestURI(), testDataFiles, roleChecker.canEdit(actor, problem));
    }

    @POST
    @Path("/testdata")
    @Consumes(MULTIPART_FORM_DATA)
    @UnitOfWork
    public Response uploadGradingTestDataFiles(
            @Context HttpServletRequest req,
            @PathParam("problemId") int problemId,
            @FormDataParam("file") InputStream fileStream,
            @FormDataParam("file") FormDataContentDisposition fileDetails,
            @FormDataParam("fileZipped") InputStream fileZippedStream) {

        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canEdit(actor, problem));

        if (fileStream != null) {
            problemStore.createUserCloneIfNotExists(actor.getUserJid(), problem.getJid());
            automatonProblemStore.uploadGradingTestDataFile(actor.getUserJid(), problem.getJid(), fileStream, fileDetails.getFileName());
        } else if (fileZippedStream != null) {
            problemStore.createUserCloneIfNotExists(actor.getUserJid(), problem.getJid());
            automatonProblemStore.uploadGradingTestDataFileZipped(actor.getUserJid(), problem.getJid(), fileZippedStream);
        }

        return redirect("/problems/automaton/" + problemId + "/grading/testdata");
    }

    @GET
    @Path("/testdata/{filename}")
    @UnitOfWork(readOnly = true)
    public Response downloadGradingTestDataFile(
            @Context HttpServletRequest req,
            @PathParam("problemId") int problemId,
            @PathParam("filename") String filename) {

        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canView(actor, problem));

        String testDataFileUrl = automatonProblemStore.getGradingTestDataFileURL(actor.getUserJid(), problem.getJid(), filename);
        return ServiceUtils.buildDownloadResponse(testDataFileUrl);
    }

    @GET
    @Path("/helpers")
    @UnitOfWork(readOnly = true)
    public View listGradingHelperFiles(@Context HttpServletRequest req, @PathParam("problemId") int problemId) {
        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canView(actor, problem));

        List<FileInfo> helperFiles = automatonProblemStore.getGradingHelperFiles(actor.getUserJid(), problem.getJid());

        HtmlTemplate template = newProblemGradingTemplate(actor, problem);
        template.setActiveSecondaryTab("helpers");
        return new ListFilesView(template, req.getRequestURI(), helperFiles, roleChecker.canEdit(actor, problem));
    }

    @POST
    @Path("/helpers")
    @Consumes(MULTIPART_FORM_DATA)
    @UnitOfWork
    public Response uploadGradingHelperFiles(
            @Context HttpServletRequest req,
            @PathParam("problemId") int problemId,
            @FormDataParam("file") InputStream fileStream,
            @FormDataParam("file") FormDataContentDisposition fileDetails,
            @FormDataParam("fileZipped") InputStream fileZippedStream) {

        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canEdit(actor, problem));

        if (fileStream != null) {
            problemStore.createUserCloneIfNotExists(actor.getUserJid(), problem.getJid());
            automatonProblemStore.uploadGradingHelperFile(actor.getUserJid(), problem.getJid(), fileStream, fileDetails.getFileName());
        } else if (fileZippedStream != null) {
            problemStore.createUserCloneIfNotExists(actor.getUserJid(), problem.getJid());
            automatonProblemStore.uploadGradingHelperFileZipped(actor.getUserJid(), problem.getJid(), fileZippedStream);
        }

        return redirect("/problems/automaton/" + problemId + "/grading/helpers");
    }

    @GET
    @Path("/helpers/{filename}")
    @UnitOfWork(readOnly = true)
    public Response downloadGradingHelperFile(
            @Context HttpServletRequest req,
            @PathParam("problemId") int problemId,
            @PathParam("filename") String filename) {

        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canView(actor, problem));

        String helperFileUrl = automatonProblemStore.getGradingHelperFileURL(actor.getUserJid(), problem.getJid(), filename);
        return ServiceUtils.buildDownloadResponse(helperFileUrl);
    }

    @GET
    @Path("/automatonRestriction")
    @UnitOfWork(readOnly = true)
    public View editAutomatonRestriction(@Context HttpServletRequest req, @PathParam("problemId") int problemId) {
        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canView(actor, problem));

        AutomatonRestriction automatonRestriction = automatonProblemStore.getAutomatonRestriction(actor.getUserJid(), problem.getJid());
        EditAutomatonRestrictionForm form = new EditAutomatonRestrictionForm();
        form.isAllowedAll = automatonRestriction.isAllowedAll();
        form.allowedAutomatons = automatonRestriction.getAllowedAutomatons();

        HtmlTemplate template = newProblemGradingTemplate(actor, problem);
        template.setActiveSecondaryTab("automatonRestriction");
        return new EditAutomatonRestrictionView(template, form, roleChecker.canEdit(actor, problem));
    }

    @POST
    @Path("/automatonRestriction")
    @UnitOfWork
    public Response updateAutomatoneRestriction(
            @Context HttpServletRequest req,
            @PathParam("problemId") int problemId,
            @BeanParam EditAutomatonRestrictionForm form) {

        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canEdit(actor, problem));

        problemStore.createUserCloneIfNotExists(actor.getUserJid(), problem.getJid());

        AutomatonRestriction automatonRestriction = AutomatonRestrictionAdapter.getAutomatonRestriction(form.isAllowedAll, form.allowedAutomatons);
        automatonProblemStore.updateAutomatonRestriction(actor.getUserJid(), problem.getJid(), automatonRestriction);

        return redirect("/problems/automaton/" + problemId + "/grading/languageRestriction");
    }

    protected HtmlTemplate newProblemGradingTemplate(Actor actor, Problem problem) {
        HtmlTemplate template = newProblemTemplate(actor, problem);
        template.setActiveMainTab("grading");
        template.addSecondaryTab("engine", "Engine", "/problems/automaton/" + problem.getId() + "/grading/engine");
        template.addSecondaryTab("config", "Config", "/problems/automaton/" + problem.getId() + "/grading/config");
        template.addSecondaryTab("testdata", "Test data", "/problems/automaton/" + problem.getId() + "/grading/testdata");
        template.addSecondaryTab("helpers", "Helpers", "/problems/automaton/" + problem.getId() + "/grading/helpers");
        template.addSecondaryTab("automatonRestriction", "Automaton restriction", "/problems/automaton/" + problem.getId() + "/grading/automatonRestriction");
        return template;
    }
}
