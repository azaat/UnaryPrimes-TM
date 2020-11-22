package formallang;

import java.util.HashSet;
import java.util.Set;

public class TmToUnrestrictedGrammar {
    public static UnrestrictedGrammar convert(TuringMachine tm) {
        Set<UnrestrictedGrammar.Production> productions = new HashSet<>();
        Set<UnrestrictedGrammar.GrammarSymbol> terminals = new HashSet<>();
        Set<UnrestrictedGrammar.GrammarSymbol> variables = new HashSet<>();
        UnrestrictedGrammar.GrammarSymbol startSymbol;

        for (TuringMachine.State state : tm.getStates()) {
            variables.add(
                new UnrestrictedGrammar.GrammarSymbol(state.getValue(), false)
            );
        }
        startSymbol = new UnrestrictedGrammar.GrammarSymbol(
            tm.getInitialState().getValue(), false
        );

        return new UnrestrictedGrammar(terminals, variables, productions, startSymbol);
    }
}
