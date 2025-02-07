//
// Generated by JTB 1.3.2
//

package diamond.parser.syntaxtree;

/**
 * Grammar production: nodeToken -> <SELECT> nodeOptional -> ( <DISTINCT> |
 * <REDUCED> )? nodeChoice -> ( ( Var() )+ | "*" ) nodeListOptional -> (
 * DatasetClause() )* whereClause -> WhereClause() solutionModifier ->
 * SolutionModifier()
 */
@SuppressWarnings("serial")
public class SelectQuery implements Node {

    public NodeToken nodeToken;
    public NodeOptional nodeOptional;
    public NodeChoice nodeChoice;
    public NodeListOptional nodeListOptional;
    public WhereClause whereClause;
    public SolutionModifier solutionModifier;

    public SelectQuery(NodeToken n0, NodeOptional n1, NodeChoice n2, NodeListOptional n3, WhereClause n4,
            SolutionModifier n5) {
        nodeToken = n0;
        nodeOptional = n1;
        nodeChoice = n2;
        nodeListOptional = n3;
        whereClause = n4;
        solutionModifier = n5;
    }

    public SelectQuery(NodeOptional n0, NodeChoice n1, NodeListOptional n2, WhereClause n3, SolutionModifier n4) {
        nodeToken = new NodeToken("select");
        nodeOptional = n0;
        nodeChoice = n1;
        nodeListOptional = n2;
        whereClause = n3;
        solutionModifier = n4;
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
