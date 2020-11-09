package utils;

import formallang.UnrestrictedGrammar;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static formallang.UnrestrictedGrammar.GrammarSymbol;
import static formallang.UnrestrictedGrammar.Production;

class GrammarUtilsTest {

    @Test
    void storeGrammar() {
        GrammarSymbol startSymb = new GrammarSymbol("S", false);
        GrammarSymbol a = new GrammarSymbol("a", true);
        GrammarSymbol b = new GrammarSymbol("b", true);
        GrammarSymbol eps = new GrammarSymbol("eps", true);

        Production prod1 = new Production(List.of(startSymb), List.of(startSymb, a, startSymb, b));
        Production prod2 = new Production(List.of(startSymb), List.of(eps));

        UnrestrictedGrammar grammar = new UnrestrictedGrammar(
                Set.of(a, b, eps),
                Set.of(startSymb),
                Set.of(prod1, prod2),
                startSymb
        );

        GrammarUtils.storeGrammar(grammar, "test_grammar.txt");
    }
}