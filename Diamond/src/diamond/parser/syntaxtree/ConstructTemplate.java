//
// Generated by JTB 1.3.2
//

package diamond.parser.syntaxtree;

/**
 * Grammar production: nodeToken -> "{" nodeOptional -> ( ConstructTriples() )?
 * nodeToken1 -> "}"
 */
@SuppressWarnings("serial")
public class ConstructTemplate implements Node {

    public NodeToken nodeToken;
    public NodeOptional nodeOptional;
    public NodeToken nodeToken1;

    public ConstructTemplate(NodeToken n0, NodeOptional n1, NodeToken n2) {
        nodeToken = n0;
        nodeOptional = n1;
        nodeToken1 = n2;
    }

    public ConstructTemplate(NodeOptional n0) {
        nodeToken = new NodeToken("{");
        nodeOptional = n0;
        nodeToken1 = new NodeToken("}");
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
