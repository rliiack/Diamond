//
// Generated by JTB 1.3.2
//

package diamond.parser.syntaxtree;

/**
 * Grammar production: nodeToken -> "(" expression -> Expression() nodeToken1 ->
 * ")"
 */
@SuppressWarnings("serial")
public class BrackettedExpression implements Node {

    public NodeToken nodeToken;
    public Expression expression;
    public NodeToken nodeToken1;

    public BrackettedExpression(NodeToken n0, Expression n1, NodeToken n2) {
        nodeToken = n0;
        expression = n1;
        nodeToken1 = n2;
    }

    public BrackettedExpression(Expression n0) {
        nodeToken = new NodeToken("(");
        expression = n0;
        nodeToken1 = new NodeToken(")");
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
