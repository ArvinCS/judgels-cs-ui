export const OutputOnlyOverrides = {
  KEY: 'OutputOnly',
  NAME: '-',
};

export const gradingLanguageNamesMap = {
  C: 'C',
  Cpp11: 'C++11',
  Cpp17: 'C++17',
  Cpp20: 'C++20',
  Go: 'Go',
  Java: 'Java 11',
  Jff: 'JFlap',
  Pascal: 'Pascal',
  Python3: 'Python 3',
  PyPy3: 'PyPy 3',
  Rust2021: 'Rust 2021',
  OutputOnly: '-',
  // for automaton engine
  FiniteStateAutomaton: 'Finite State Automaton',
  Mealy: 'Mealy',
  Moore: 'Moore',
  PushDownAutomaton: 'Push Down Automaton',
  Turing: 'Turing Machine',
  UnboundGrammar: 'Unbound Grammar',
};

export const automatonNamesMap = {
  FiniteStateAutomaton: 'Finite State Automaton',
  Mealy: 'Mealy',
  Moore: 'Moore',
  PushDownAutomaton: 'Push Down Automaton',
  Turing: 'Turing Machine',
  UnboundGrammar: 'Unbound Grammar',
};

export const gradingLanguageFamiliesMap = {
  C: 'C',
  Cpp11: 'C++',
  Cpp17: 'C++',
  Cpp20: 'C++',
  Go: 'Go',
  Java: 'Java',
  Jff: 'JFlap',
  Pascal: 'Pascal',
  Python3: 'Python',
  PyPy3: 'Python',
  Rust2021: 'Rust',
  // for automaton engine
  FiniteStateAutomaton: 'Finite State Automaton',
  Mealy: 'Mealy',
  Moore: 'Moore',
  PushDownAutomaton: 'Push Down Automaton',
  Turing: 'Turing Machine',
  UnboundGrammar: 'Unbound Grammar',
};

export const automatonFamiliesMap = {
  FiniteStateAutomaton: 'Finite State Automaton',
  Mealy: 'Mealy',
  Moore: 'Moore',
  PushDownAutomaton: 'Push Down Automaton',
  Turing: 'Turing Machine',
  UnboundGrammar: 'Unbound Grammar',
};

export const gradingLanguageFilenameExtensionsMap = {
  C: ['c'],
  Cpp11: ['cc', 'cpp'],
  Cpp17: ['cc', 'cpp'],
  Cpp20: ['cc', 'cpp'],
  Go: ['go'],
  Java: ['java'],
  Jff: ['jff'],
  Pascal: ['pas'],
  Python3: ['py'],
  PyPy3: ['py'],
  Rust2021: ['rs'],
  OutputOnly: ['zip'],
  // for automaton engine
  FiniteStateAutomaton: ['jff'],
  Mealy: ['jff'],
  Moore: ['jff'],
  PushDownAutomaton: ['jff'],
  Turing: ['jff'],
  UnboundGrammar: ['jff'],
};

export const automatonFilenameExtensionsMap = {
  FiniteStateAutomaton: ['jff'],
  Mealy: ['jff'],
  Moore: ['jff'],
  PushDownAutomaton: ['jff'],
  Turing: ['jff'],
  UnboundGrammar: ['jff'],
};

export const gradingLanguageSyntaxHighlighterValueMap = {
  C: 'c',
  Cpp11: 'cpp',
  Cpp17: 'cpp',
  Cpp20: 'cpp',
  Go: 'go',
  Java: 'java',
  Jff: 'xml',
  Pascal: 'pascal',
  Python3: 'python',
  PyPy3: 'python',
  Rust2021: 'rust',
  OutputOnly: '',
  // for automaton engine
  FiniteStateAutomaton: 'xml',
  Mealy: 'xml',
  Moore: 'xml',
  PushDownAutomaton: 'xml',
  Turing: 'xml',
  UnboundGrammar: 'xml',
};

export const gradingLanguageEditorSubmissionFilenamesMap = {
  C: 'solution.c',
  Cpp11: 'solution.cpp',
  Cpp17: 'solution.cpp',
  Cpp20: 'solution.cpp',
  Go: 'solution.go',
  Java: 'Solution.java',
  Jff: 'solution.jff',
  Pascal: 'solution.pas',
  Python3: 'solution.py',
  PyPy3: 'solution.py',
  Rust2021: 'solution.rs',
  // for automaton engine
  FiniteStateAutomaton: 'solution.jff',
  Mealy: 'solution.jff',
  Moore: 'solution.jff',
  PushDownAutomaton: 'solution.jff',
  Turing: 'solution.jff',
  UnboundGrammar: 'solution.jff',
};

export const gradingLanguageEditorSubmissionHintsMap = {
  Java: 'Public class name must be Solution',
};

export const gradingLanguages = Object.keys(gradingLanguageNamesMap)
  .filter(l => l !== OutputOnlyOverrides.KEY)
  .sort();

export function getGradingLanguageName(code) {
  return gradingLanguageNamesMap[code] || code;
}

export function getAutomatonName(code) {
  return automatonNamesMap[code] || code;
}

export function getGradingLanguageFamily(code) {
  return gradingLanguageFamiliesMap[code];
}

export function getAutomatonFamily(code) {
  return automatonFamiliesMap[code];
}

export function getGradingLanguageFilenameExtensions(code) {
  return gradingLanguageFilenameExtensionsMap[code] || [];
}

export function getAutomatonFilenameExtensions(code) {
  return ['.jff'];
}

export function getGradingLanguageSyntaxHighlighterValue(code) {
  return gradingLanguageSyntaxHighlighterValueMap[code] || code;
}

export function getAutomatonSyntaxHighlighterValue(code) {
  return 'xml';
}

export function getGradingLanguageEditorSubmissionFilename(code) {
  return gradingLanguageEditorSubmissionFilenamesMap[code];
}

export function getAutomatonEditorSubmissionFilename(code) {
  return 'solution.jff';
}

export function getGradingLanguageEditorSubmissionHint(code) {
  return gradingLanguageEditorSubmissionHintsMap[code];
}

export function getAllowedGradingLanguages(gradingEngine, restriction) {
  if (gradingEngine.startsWith(OutputOnlyOverrides.KEY)) {
    return [OutputOnlyOverrides.KEY];
  }
  if (restriction.allowedLanguageNames.length === 0) {
    if (gradingEngine.startsWith('Automata')) {
      return Object.keys(gradingLanguageNamesMap)
        .filter(l => l !== OutputOnlyOverrides.KEY)
        .slice(-6);
    } else {
      return Object.keys(gradingLanguageNamesMap)
        .filter(l => l !== OutputOnlyOverrides.KEY)
        .slice(0, -6);
    }
  }
  return restriction.allowedLanguageNames;
}

// export function getAllowedAutomatons(gradingEngine, restriction) {
//   if (restriction.allowedAutomatonNames.length === 0) {
//     return automatons;
//   }
//   return restriction.allowedAutomatonNames;
// }

export function allLanguagesAllowed(r) {
  return !r.allowedLanguageNames || r.allowedLanguageNames.length === 0;
}

export function allAutomatonsAllowed(r) {
  return !r.allowedAutomatonNames || r.allowedAutomatonNames.length === 0;
}
