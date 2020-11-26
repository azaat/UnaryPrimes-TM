package formallang;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class UnrestrictedGrammar {
    private final Set<GrammarSymbol> terminals;
    private final Set<GrammarSymbol> variables;
    private final Set<Production> productions;
    private final GrammarSymbol startSymbol;

    public UnrestrictedGrammar(
            Set<GrammarSymbol> terminals,
            Set<GrammarSymbol> variables,
            Set<Production> productions,
            GrammarSymbol startSymbol
    ) {
        this.terminals = terminals;
        this.variables = variables;
        this.productions = productions;
        this.startSymbol = startSymbol;
    }

    public UnrestrictedGrammar(Set<Production> productions, GrammarSymbol startSymbol) {
        this.productions = productions;
        this.startSymbol = startSymbol;

        Set<GrammarSymbol> terminals = new HashSet<>();
        Set<GrammarSymbol> variables = new HashSet<>();

        for (var prod : productions) {
            for (var sym : prod.getHead()) {
                if (sym.isTerminal) {
                    terminals.add(sym);
                } else {
                    variables.add(sym);
                }
            }
            for (var sym : prod.getBody()) {
                if (sym.isTerminal) {
                    terminals.add(sym);
                } else {
                    variables.add(sym);
                }
            }
        }

        this.terminals = terminals;
        this.variables = variables;
    }

    public Set<GrammarSymbol> getTerminals() {
        return terminals;
    }

    public Set<GrammarSymbol> getVariables() {
        return variables;
    }

    public Set<Production> getProductions() {
        return productions;
    }

    public GrammarSymbol getStartSymbol() {
        return startSymbol;
    }

    public static class Production {
        public enum Type {
            TAPE_GENERATING,
            TM_EMULATING,
            WORD_RESTORING
        }

        private final List<GrammarSymbol> head;
        private final List<GrammarSymbol> body;
        private final Type type;

        public Production(List<GrammarSymbol> head, List<GrammarSymbol> body, Type type) {
            this.head = head;
            this.body = body;
            this.type = type;
        }

        public List<GrammarSymbol> getHead() {
            return head;
        }

        public List<GrammarSymbol> getBody() {
            return body;
        }

        public Type getType() {
            return type;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Production that = (Production) o;
            return Objects.equals(head, that.head) &&
                    Objects.equals(body, that.body);
        }

        @Override
        public int hashCode() {
            return Objects.hash(head, body);
        }

        @Override
        public String toString() {
            return head.toString() + " -> " + body.toString();
        }
    }

    public static class GrammarSymbol {
        private final String value;
        private final boolean isTerminal;

        @Override
        public String toString() {
            return value;
        }

        public GrammarSymbol(String value, boolean isTerminal) {
            this.value = value;
            this.isTerminal = isTerminal;
        }

        public String getValue() {
            return value;
        }

        public boolean isTerminal() {
            return isTerminal;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            GrammarSymbol that = (GrammarSymbol) o;
            return isTerminal == that.isTerminal &&
                    value.equals(that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value, isTerminal);
        }
    }
}
