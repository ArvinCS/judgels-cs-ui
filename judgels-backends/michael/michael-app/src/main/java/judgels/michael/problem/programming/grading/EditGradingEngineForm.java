package judgels.michael.problem.programming.grading;

import javax.ws.rs.FormParam;
import judgels.michael.template.HtmlForm;

public class EditGradingEngineForm extends HtmlForm {
    @FormParam("gradingEngine")
    String gradingEngine;

    public String getGradingEngine() {
        return gradingEngine;
    }
}
