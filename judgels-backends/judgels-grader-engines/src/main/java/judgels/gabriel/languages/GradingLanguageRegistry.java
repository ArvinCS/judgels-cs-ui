package judgels.gabriel.languages;

import com.google.common.collect.ImmutableList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import judgels.gabriel.api.AutomatonMachine;
import judgels.gabriel.api.GradingLanguage;
import judgels.gabriel.languages.automatons.FiniteStateAutomatonMachine;
import judgels.gabriel.languages.automatons.MealyMachine;
import judgels.gabriel.languages.automatons.MooreMachine;
import judgels.gabriel.languages.automatons.PushDownAutomatonMachine;
import judgels.gabriel.languages.automatons.TuringMachine;
import judgels.gabriel.languages.automatons.UnboundGrammarMachine;
import judgels.gabriel.languages.c.CGradingLanguage;
import judgels.gabriel.languages.cpp.Cpp11GradingLanguage;
import judgels.gabriel.languages.cpp.Cpp17GradingLanguage;
import judgels.gabriel.languages.cpp.Cpp20GradingLanguage;
import judgels.gabriel.languages.cpp.CppGradingLanguage;
import judgels.gabriel.languages.go.GoGradingLanguage;
import judgels.gabriel.languages.java.JavaGradingLanguage;
import judgels.gabriel.languages.jff.JffGradingLanguage;
import judgels.gabriel.languages.pascal.PascalGradingLanguage;
import judgels.gabriel.languages.python.PyPy3GradingLanguage;
import judgels.gabriel.languages.python.Python3GradingLanguage;
import judgels.gabriel.languages.rust.Rust2021GradingLanguage;
import judgels.gabriel.languages.sql.SqlGradingLanguage;

public class GradingLanguageRegistry {
    private static final GradingLanguageRegistry INSTANCE = new GradingLanguageRegistry();

    private static final List<GradingLanguage> LANGUAGES = ImmutableList.of(
            new CGradingLanguage(),
            new CppGradingLanguage(),
            new Cpp11GradingLanguage(),
            new Cpp17GradingLanguage(),
            new Cpp20GradingLanguage(),
            new GoGradingLanguage(),
            new JavaGradingLanguage(),
            new JffGradingLanguage(),
            new PascalGradingLanguage(),
            new PyPy3GradingLanguage(),
            new Python3GradingLanguage(),
            new Rust2021GradingLanguage(),
            new SqlGradingLanguage(),
            new OutputOnlyGradingLanguage());

    private static final List<AutomatonMachine> AUTOMATONS = ImmutableList.of(
            new FiniteStateAutomatonMachine(),
            new MealyMachine(),
            new MooreMachine(),
            new PushDownAutomatonMachine(),
            new TuringMachine(),
            new UnboundGrammarMachine());

    private static final List<GradingLanguage> VISIBLE_LANGUAGES = LANGUAGES.stream()
                    .filter(GradingLanguage::isVisible)
                    .collect(Collectors.toList());
    private static final List<AutomatonMachine> VISIBLE_AUTOMATONS = AUTOMATONS.stream()
                    .filter(AutomatonMachine::isAutomaton)
                    .collect(Collectors.toList());

    private static final Map<String, GradingLanguage> LANGUAGES_BY_SIMPLE_NAME = new LinkedHashMap<>();
    private static final Map<String, AutomatonMachine> AUTOMATONS_BY_SIMPLE_NAME = new LinkedHashMap<>();
    private static final Map<String, String> LANGUAGE_NAMES_BY_SIMPLE_NAME = new LinkedHashMap<>();
    private static final Map<String, String> AUTOMATONS_NAMES_BY_SIMPLE_NAME = new LinkedHashMap<>();
    private static final Map<String, String> VISIBLE_LANGUAGE_NAMES_BY_SIMPLE_NAME = new LinkedHashMap<>();
    private static final Map<String, String> VISIBLE_AUTOMATONS_NAMES_BY_SIMPLE_NAME = new LinkedHashMap<>();

    static {
        LANGUAGES.stream().forEach(language -> {
            LANGUAGES_BY_SIMPLE_NAME.put(getSimpleNameGradingLanguage(language), language);
            LANGUAGE_NAMES_BY_SIMPLE_NAME.put(getSimpleNameGradingLanguage(language), language.getName());
        });
        AUTOMATONS.stream().forEach(automaton -> {
            AUTOMATONS_BY_SIMPLE_NAME.put(getSimpleNameAutomatonMachine(automaton), automaton);
            AUTOMATONS_NAMES_BY_SIMPLE_NAME.put(getSimpleNameAutomatonMachine(automaton), automaton.getName());
        });
        VISIBLE_LANGUAGES.stream().forEach(language -> {
            VISIBLE_LANGUAGE_NAMES_BY_SIMPLE_NAME.put(getSimpleNameGradingLanguage(language), language.getName());
        });
        VISIBLE_AUTOMATONS.stream().forEach(automata -> {
            VISIBLE_AUTOMATONS_NAMES_BY_SIMPLE_NAME.put(getSimpleNameAutomatonMachine(automata), automata.getName());
        });
    }

    private GradingLanguageRegistry() {}

    public static GradingLanguageRegistry getInstance() {
        return INSTANCE;
    }

    public GradingLanguage get(String simpleName) {
        GradingLanguage language = LANGUAGES_BY_SIMPLE_NAME.get(simpleName);
        if (language == null) {
            language = (GradingLanguage) AUTOMATONS_BY_SIMPLE_NAME.get(simpleName);
        }
        if (language == null) {
            throw new IllegalArgumentException("Grading language " + simpleName + " not found");
        }
        return language;
    }

    public Map<String, String> getLanguages() {
        return LANGUAGE_NAMES_BY_SIMPLE_NAME;
    }

    public Map<String, String> getAutomatons() {
        return AUTOMATONS_NAMES_BY_SIMPLE_NAME;
    }

    public Map<String, String> getVisibleLanguages() {
        return VISIBLE_LANGUAGE_NAMES_BY_SIMPLE_NAME;
    }

    public Map<String, String> getVisibleAutomatons() {
        return VISIBLE_AUTOMATONS_NAMES_BY_SIMPLE_NAME;
    }

    private static String getSimpleNameGradingLanguage(GradingLanguage language) {
        String name = language.getClass().getSimpleName();
        System.out.println(name);
        return name.substring(0, name.length() - "GradingLanguage".length());
    }

    private static String getSimpleNameAutomatonMachine(AutomatonMachine machine) {
        String name = machine.getClass().getSimpleName();
        return name.substring(0, name.length() - "Machine".length());
    }
}
