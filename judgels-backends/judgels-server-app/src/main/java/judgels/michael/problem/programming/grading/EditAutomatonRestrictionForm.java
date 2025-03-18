package judgels.michael.problem.programming.grading;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.FormParam;
import judgels.michael.template.HtmlForm;

public class EditAutomatonRestrictionForm extends HtmlForm {
    @FormParam("isAllowedAll")
    boolean isAllowedAll;

    @FormParam("allowedAutomatons")
    Set<String> allowedAutomatons = new HashSet<>();

    public boolean getIsAllowedAll() {
        return isAllowedAll;
    }

    public Set<String> getAllowedAutomatons() {
        return allowedAutomatons;
    }
}
