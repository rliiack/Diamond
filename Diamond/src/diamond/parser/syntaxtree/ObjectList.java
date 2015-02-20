//
// Generated by JTB 1.3.2
//

package diamond.parser.syntaxtree;

/**
 * Grammar production: sparqlObject -> SparqlObject() nodeListOptional -> ( ","
 * SparqlObject() )*
 */
@SuppressWarnings("serial")
public class ObjectList implements Node {

    public SparqlObject sparqlObject;
    public NodeListOptional nodeListOptional;

    public ObjectList(SparqlObject n0, NodeListOptional n1) {
        sparqlObject = n0;
        nodeListOptional = n1;
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
