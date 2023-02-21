package judgels.michael.index;

import judgels.michael.template.HtmlForm;
import judgels.michael.template.HtmlTemplate;
import judgels.michael.template.TemplateView;

public class LoginView extends TemplateView {
    public LoginView(HtmlTemplate template, HtmlForm form) {
        super("loginView.ftl", template, form);
    }
}
