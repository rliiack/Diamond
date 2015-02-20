//
// Generated by JTB 1.3.2
//

package diamond.parser.syntaxtree;

/**
 * Grammar production: nodeChoice -> <VAR1> | <VAR2>
 */
@SuppressWarnings("serial")
public class Var implements Node {

    public NodeChoice nodeChoice;

    public Var(NodeChoice n0) {
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
