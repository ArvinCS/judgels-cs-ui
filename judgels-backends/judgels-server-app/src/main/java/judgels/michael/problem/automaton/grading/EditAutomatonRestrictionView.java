package judgels.michael.problem.automaton.grading;

import java.util.Map;
import judgels.gabriel.languages.GradingLanguageRegistry;
import judgels.michael.template.HtmlTemplate;
import judgels.michael.template.TemplateView;

public class EditAutomatonRestrictionView extends TemplateView {
    private final boolean canEdit;

    public EditAutomatonRestrictionView(
            HtmlTemplate template,
            EditAutomatonRestrictionForm form,
            boolean canEdit) {

        super("editAutomatonRestrictionView.ftl", template, form);
        this.canEdit = canEdit;
    }

    public Map<String, String> getLanguages() {
        return GradingLanguageRegistry.getInstance().getVisibleAutomatons();
    }

    public boolean getCanEdit() {
        return canEdit;
    }
}
