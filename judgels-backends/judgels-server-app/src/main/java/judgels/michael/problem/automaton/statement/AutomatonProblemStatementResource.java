package judgels.michael.problem.automaton.statement;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.views.View;
import java.util.Set;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import judgels.gabriel.api.AutomatonRestriction;
import judgels.gabriel.api.GradingConfig;
import judgels.jophiel.api.actor.Actor;
import judgels.michael.problem.automaton.BaseAutomatonProblemResource;
import judgels.michael.problem.automaton.grading.AutomatonRestrictionAdapter;
import judgels.michael.template.HtmlTemplate;
import judgels.sandalphon.api.problem.Problem;
import judgels.sandalphon.api.problem.ProblemStatement;

@Path("/problems/automaton/{problemId}/statements")
public class AutomatonProblemStatementResource extends BaseAutomatonProblemResource {
    @Inject public AutomatonProblemStatementResource() {}

    @GET
    @UnitOfWork(readOnly = true)
    public View viewStatement(@Context HttpServletRequest req, @PathParam("problemId") int problemId) {
        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canView(actor, problem));

        Set<String> enabledLanguages = statementStore.getStatementEnabledLanguages(actor.getUserJid(), problem.getJid());
        String language = resolveStatementLanguage(req, actor, problem, enabledLanguages);
        ProblemStatement statement = statementStore.getStatement(actor.getUserJid(), problem.getJid(), language);

        String gradingEngine = automatonProblemStore.getGradingEngine(actor.getUserJid(), problem.getJid());
        GradingConfig gradingConfig = automatonProblemStore.getGradingConfig(actor.getUserJid(), problem.getJid());
        AutomatonRestriction gradingLanguageRestriction = automatonProblemStore.getAutomatonRestriction(actor.getUserJid(), problem.getJid());
        Set<String> allowedGradingLanguages = AutomatonRestrictionAdapter.getAllowedAutomatons(gradingLanguageRestriction);

        String reasonNotAllowedToSubmit = roleChecker.canSubmit(actor, problem).orElse("");
        boolean canSubmit = reasonNotAllowedToSubmit.isEmpty();

        HtmlTemplate template = newProblemStatementTemplate(actor, problem);
        template.setActiveSecondaryTab("view");
        return new ViewStatementView(template, statement, language, enabledLanguages, gradingConfig, gradingEngine, allowedGradingLanguages, reasonNotAllowedToSubmit, canSubmit);
    }
}
