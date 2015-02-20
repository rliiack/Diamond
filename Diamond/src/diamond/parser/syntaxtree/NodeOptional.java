//
// Generated by JTB 1.3.2
//

package diamond.parser.syntaxtree;

/**
 * Represents an grammar optional node, e.g. ( A )? or [ A ]
 */
@SuppressWarnings("serial")
public class NodeOptional implements Node {

    public NodeOptional() {
        node = null;
    }

    public NodeOptional(Node n) {
        addNode(n);
    }

    public void addNode(Node n) {
        if (node != null) // Oh oh!
            throw new Error("Attempt to set optional node twice");

        node = n;
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

    public boolean present() {
        return node != null;
    }

    public Node node;
}
