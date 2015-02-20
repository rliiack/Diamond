//
// Generated by JTB 1.3.2
//

package diamond.parser.syntaxtree;

/**
 * Grammar production: nodeToken -> <REGEX> nodeToken1 -> "(" expression ->
 * Expression() nodeToken2 -> "," expression1 -> Expression() nodeOptional -> (
 * "," Expression() )? nodeToken3 -> ")"
 */
@SuppressWarnings("serial")
public class RegexExpression implements Node {

    public NodeToken nodeToken;
    public NodeToken nodeToken1;
    public Expression expression;
    public NodeToken nodeToken2;
    public Expression expression1;
    public NodeOptional nodeOptional;
    public NodeToken nodeToken3;

    public RegexExpression(NodeToken n0, NodeToken n1, Expression n2, NodeToken n3, Expression n4, NodeOptional n5,
            NodeToken n6) {
        nodeToken = n0;
        nodeToken1 = n1;
        expression = n2;
        nodeToken2 = n3;
        expression1 = n4;
        nodeOptional = n5;
        nodeToken3 = n6;
    }

    public RegexExpression(Expression n0, Expression n1, NodeOptional n2) {
        nodeToken = new NodeToken("regex");
        nodeToken1 = new NodeToken("(");
        expression = n0;
        nodeToken2 = new NodeToken(",");
        expression1 = n1;
        nodeOptional = n2;
        nodeToken3 = new NodeToken(")");
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
