package judgels.michael.lesson.statement;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import judgels.michael.template.HtmlTemplate;
import judgels.michael.template.TemplateView;
import judgels.sandalphon.api.lesson.Lesson;
import judgels.sandalphon.api.lesson.LessonStatement;
import judgels.sandalphon.resource.WorldLanguageRegistry;

public class ViewStatementView extends TemplateView {
    private final Lesson lesson;
    private final LessonStatement statement;
    private final String language;
    private final Set<String> enabledLanguages;

    public ViewStatementView(
            HtmlTemplate template,
            Lesson lesson,
            LessonStatement statement,
            String language,
            Set<String> enabledLanguages) {

        super("viewStatementView.ftl", template);
        this.lesson = lesson;
        this.statement = statement;
        this.language = language;
        this.enabledLanguages = enabledLanguages;
    }

    public Lesson getLesson() {
        return lesson;
    }

    public LessonStatement getStatement() {
        return statement;
    }

    public String getLanguage() {
        return language;
    }

    public Map<String, String> getEnabledLanguages() {
        Map<String, String> languages = new LinkedHashMap<>();
        for (String lang : enabledLanguages) {
            languages.put(lang, WorldLanguageRegistry.getInstance().getLanguages().get(lang));
        }
        return languages;
    }
}
