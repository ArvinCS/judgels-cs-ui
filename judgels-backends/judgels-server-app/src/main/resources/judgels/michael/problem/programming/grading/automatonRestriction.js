document.addEventListener('DOMContentLoaded', () => {
  if (document.getElementById('isAllowedAll').checked) {
    document.querySelectorAll('.allowedAutomaton').forEach(automaton => {
      automaton.disabled = true;
      automaton.checked = true;
    });
  }

  document.getElementById('isAllowedAll').addEventListener('click', () => {
    if (document.getElementById('isAllowedAll').checked) {
      document.querySelectorAll('.allowedAutomaton').forEach(automaton => {
        automaton.disabled = true;
        automaton.checked = true;
      });
    } else {
      document.querySelectorAll('.allowedAutomaton').forEach(automaton => {
        automaton.disabled = false;
        automaton.checked = false;
      });
    }
  });
}, false);
