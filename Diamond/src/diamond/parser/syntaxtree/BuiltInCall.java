//
// Generated by JTB 1.3.2
//

package diamond.parser.syntaxtree;

/**
 * Grammar production: nodeChoice -> ( <STR> "(" Expression() ")" | <LANG> "("
 * Expression() ")" | <LANGMATCHES> "(" Expression() "," Expression() ")" |
 * <DATATYPE> "(" Expression() ")" | <BOUND> "(" Var() ")" | <SAME_TERM> "("
 * Expression() "," Expression() ")" | <IS_IRI> "(" Expression() ")" | <IS_URI>
 * "(" Expression() ")" | <IS_BLANK> "(" Expression() ")" | <IS_LITERAL> "("
 * Expression() ")" | RegexExpression() )
 */
@SuppressWarnings("serial")
public class BuiltInCall implements Node {

    public NodeChoice nodeChoice;

    public BuiltInCall(NodeChoice n0) {
        nodeChoice = n0;
    }

    @Override
    public void accept(diamond.parser.visitor.Visitor v) {
        v.visit(this);
    }

    @Override
    public <R, A> R accept(diamond.parser.visitor.GJVisitor<R, A> v, A argu) {
        return v.visit(this, argu);
    }

    @Override
    public <R> R accept(diamond.parser.visitor.GJNoArguVisitor<R> v) {
        return v.visit(this);
    }

    @Override
    public <A> void accept(diamond.parser.visitor.GJVoidVisitor<A> v, A argu) {
        v.visit(this, argu);
    }
}
