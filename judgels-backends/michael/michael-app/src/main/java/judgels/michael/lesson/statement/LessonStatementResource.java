package judgels.michael.lesson.statement;

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
import judgels.jophiel.api.actor.Actor;
import judgels.michael.lesson.BaseLessonResource;
import judgels.michael.template.HtmlTemplate;
import judgels.sandalphon.api.lesson.Lesson;
import judgels.sandalphon.api.lesson.LessonStatement;

@Path("/lessons/{lessonId}/statements")
public class LessonStatementResource extends BaseLessonResource {
    @Inject public LessonStatementResource() {}

    @GET
    @UnitOfWork(readOnly = true)
    public View viewStatement(
            @Context HttpServletRequest req,
            @PathParam("lessonId") int lessonId) {

        Actor actor = actorChecker.check(req);
        Lesson lesson = checkFound(lessonStore.findLessonById(lessonId));
        checkAllowed(lessonRoleChecker.canView(actor, lesson));

        Set<String> enabledLanguages = lessonStore.getEnabledLanguages(actor.getUserJid(), lesson.getJid());
        String language = resolveStatementLanguage(req, actor, lesson, enabledLanguages);
        LessonStatement statement = lessonStore.getStatement(actor.getUserJid(), lesson.getJid(), language);

        HtmlTemplate template = newLessonStatementTemplate(actor, lesson);
        template.setActiveSecondaryTab("view");
        return new ViewStatementView(template, lesson, statement, language, enabledLanguages);
    }
}
