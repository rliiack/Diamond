//
// Generated by JTB 1.3.2
//

package diamond.parser.syntaxtree;

/**
 * Grammar production: nodeToken -> <DESCRIBE> nodeChoice -> ( ( VarOrIRIref()
 * )+ | "*" ) nodeListOptional -> ( DatasetClause() )* nodeOptional -> (
 * WhereClause() )? solutionModifier -> SolutionModifier()
 */
@SuppressWarnings("serial")
public class DescribeQuery implements Node {

    public NodeToken nodeToken;
    public NodeChoice nodeChoice;
    public NodeListOptional nodeListOptional;
    public NodeOptional nodeOptional;
    public SolutionModifier solutionModifier;

    public DescribeQuery(NodeToken n0, NodeChoice n1, NodeListOptional n2, NodeOptional n3, SolutionModifier n4) {
        nodeToken = n0;
        nodeChoice = n1;
        nodeListOptional = n2;
        nodeOptional = n3;
        solutionModifier = n4;
    }

    public DescribeQuery(NodeChoice n0, NodeListOptional n1, NodeOptional n2, SolutionModifier n3) {
        nodeToken = new NodeToken("describe");
        nodeChoice = n0;
        nodeListOptional = n1;
        nodeOptional = n2;
        solutionModifier = n3;
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
