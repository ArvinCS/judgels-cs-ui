package judgels.michael.problem.programming.grading;

import java.util.LinkedHashSet;
import java.util.Set;
import judgels.gabriel.api.AutomatonRestriction;
import judgels.gabriel.languages.GradingLanguageRegistry;

public class AutomatonRestrictionAdapter {
    private AutomatonRestrictionAdapter() {}

    public static Set<String> getAllowedAutomatons(AutomatonRestriction automatonRestriction) {
        Set<String> languages = new LinkedHashSet<>(GradingLanguageRegistry.getInstance().getVisibleAutomatons().keySet());
        if (!automatonRestriction.isAllowedAll()) {
            languages.retainAll(automatonRestriction.getAllowedAutomatons());
        }
        return languages;
    }

    public static AutomatonRestriction getAutomatonRestriction(boolean isAllowedAll, Set<String> allowedAutomatons) {
        if (isAllowedAll) {
            return AutomatonRestriction.noRestriction();
        }
        return AutomatonRestriction.of(allowedAutomatons);
    }
}
