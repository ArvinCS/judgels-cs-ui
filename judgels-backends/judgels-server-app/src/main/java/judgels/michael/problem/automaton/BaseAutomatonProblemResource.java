package judgels.michael.problem.automaton;

import javax.inject.Inject;
import judgels.michael.problem.BaseProblemResource;
import judgels.sandalphon.problem.automaton.AutomatonProblemStore;

public abstract class BaseAutomatonProblemResource extends BaseProblemResource {
    @Inject protected AutomatonProblemStore automatonProblemStore;
}
