//
// Generated by JTB 1.3.2
//

package diamond.parser.syntaxtree;

/**
 * Grammar production: nodeOptional -> ( <WHERE> )? groupGraphPattern ->
 * GroupGraphPattern()
 */
@SuppressWarnings("serial")
public class WhereClause implements Node {

    public NodeOptional nodeOptional;
    public GroupGraphPattern groupGraphPattern;

    public WhereClause(NodeOptional n0, GroupGraphPattern n1) {
        nodeOptional = n0;
        groupGraphPattern = n1;
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
