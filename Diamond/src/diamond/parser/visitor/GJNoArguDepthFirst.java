//
// Generated by JTB 1.3.2
//

package diamond.parser.visitor;

import diamond.parser.syntaxtree.*;
import java.util.*;

/**
 * Provides default methods which visit each node in the tree in depth-first
 * order. Your visitors may extend this class.
 */
public class GJNoArguDepthFirst<R> implements GJNoArguVisitor<R> {

    //
    // Auto class visitors--probably don't need to be overridden.
    //
    @Override
    public R visit(NodeList n) {
        R _ret = null;
        for (Enumeration<Node> e = n.elements(); e.hasMoreElements();) {
            e.nextElement().accept(this);
        }
        return _ret;
    }

    @Override
    public R visit(NodeListOptional n) {
        if (n.present()) {
            R _ret = null;
            for (Enumeration<Node> e = n.elements(); e.hasMoreElements();) {
                e.nextElement().accept(this);
            }
            return _ret;
        } else
            return null;
    }

    @Override
    public R visit(NodeOptional n) {
        if (n.present())
            return n.node.accept(this);
        else
            return null;
    }

    @Override
    public R visit(NodeSequence n) {
        R _ret = null;
        for (Enumeration<Node> e = n.elements(); e.hasMoreElements();) {
            e.nextElement().accept(this);
        }
        return _ret;
    }

    @Override
    public R visit(NodeToken n) {
        return null;
    }

    //
    // User-generated visitor methods below
    //

    /**
     * prologue -> Prologue() nodeChoice -> ( SelectQuery() | ConstructQuery() |
     * DescribeQuery() | AskQuery() )
     */
    @Override
    public R visit(Query n) {
        R _ret = null;
        n.prologue.accept(this);
        n.nodeChoice.accept(this);
        return _ret;
    }

    /**
     * nodeOptional -> ( BaseDecl() )? nodeListOptional -> ( PrefixDecl() )*
     */
    @Override
    public R visit(Prologue n) {
        R _ret = null;
        n.nodeOptional.accept(this);
        n.nodeListOptional.accept(this);
        return _ret;
    }

    /**
     * nodeToken -> <BASE> nodeToken1 -> <IRI_REF>
     */
    @Override
    public R visit(BaseDecl n) {
        R _ret = null;
        n.nodeToken.accept(this);
        n.nodeToken1.accept(this);
        return _ret;
    }

    /**
     * nodeToken -> <PREFIX> nodeToken1 -> <PNAME_NS> nodeToken2 -> <IRI_REF>
     */
    @Override
    public R visit(PrefixDecl n) {
        R _ret = null;
        n.nodeToken.accept(this);
        n.nodeToken1.accept(this);
        n.nodeToken2.accept(this);
        return _ret;
    }

    /**
     * nodeToken -> <SELECT> nodeOptional -> ( <DISTINCT> | <REDUCED> )?
     * nodeChoice -> ( ( Var() )+ | "*" ) nodeListOptional -> ( DatasetClause()
     * )* whereClause -> WhereClause() solutionModifier -> SolutionModifier()
     */
    @Override
    public R visit(SelectQuery n) {
        R _ret = null;
        n.nodeToken.accept(this);
        n.nodeOptional.accept(this);
        n.nodeChoice.accept(this);
        n.nodeListOptional.accept(this);
        n.whereClause.accept(this);
        n.solutionModifier.accept(this);
        return _ret;
    }

    /**
     * nodeToken -> <CONSTRUCT> constructTemplate -> ConstructTemplate()
     * nodeListOptional -> ( DatasetClause() )* whereClause -> WhereClause()
     * solutionModifier -> SolutionModifier()
     */
    @Override
    public R visit(ConstructQuery n) {
        R _ret = null;
        n.nodeToken.accept(this);
        n.constructTemplate.accept(this);
        n.nodeListOptional.accept(this);
        n.whereClause.accept(this);
        n.solutionModifier.accept(this);
        return _ret;
    }

    /**
     * nodeToken -> <DESCRIBE> nodeChoice -> ( ( VarOrIRIref() )+ | "*" )
     * nodeListOptional -> ( DatasetClause() )* nodeOptional -> ( WhereClause()
     * )? solutionModifier -> SolutionModifier()
     */
    @Override
    public R visit(DescribeQuery n) {
        R _ret = null;
        n.nodeToken.accept(this);
        n.nodeChoice.accept(this);
        n.nodeListOptional.accept(this);
        n.nodeOptional.accept(this);
        n.solutionModifier.accept(this);
        return _ret;
    }

    /**
     * nodeToken -> <ASK> nodeListOptional -> ( DatasetClause() )* whereClause
     * -> WhereClause()
     */
    @Override
    public R visit(AskQuery n) {
        R _ret = null;
        n.nodeToken.accept(this);
        n.nodeListOptional.accept(this);
        n.whereClause.accept(this);
        return _ret;
    }

    /**
     * nodeToken -> <FROM> nodeChoice -> ( DefaultGraphClause() |
     * NamedGraphClause() )
     */
    @Override
    public R visit(DatasetClause n) {
        R _ret = null;
        n.nodeToken.accept(this);
        n.nodeChoice.accept(this);
        return _ret;
    }

    /**
     * sourceSelector -> SourceSelector()
     */
    @Override
    public R visit(DefaultGraphClause n) {
        R _ret = null;
        n.sourceSelector.accept(this);
        return _ret;
    }

    /**
     * nodeToken -> <NAMED> sourceSelector -> SourceSelector()
     */
    @Override
    public R visit(NamedGraphClause n) {
        R _ret = null;
        n.nodeToken.accept(this);
        n.sourceSelector.accept(this);
        return _ret;
    }

    /**
     * iRIref -> IRIref()
     */
    @Override
    public R visit(SourceSelector n) {
        R _ret = null;
        n.iRIref.accept(this);
        return _ret;
    }

    /**
     * nodeOptional -> ( <WHERE> )? groupGraphPattern -> GroupGraphPattern()
     */
    @Override
    public R visit(WhereClause n) {
        R _ret = null;
        n.nodeOptional.accept(this);
        n.groupGraphPattern.accept(this);
        return _ret;
    }

    /**
     * nodeOptional -> ( OrderClause() )? nodeOptional1 -> (
     * LimitOffsetClauses() )?
     */
    @Override
    public R visit(SolutionModifier n) {
        R _ret = null;
        n.nodeOptional.accept(this);
        n.nodeOptional1.accept(this);
        return _ret;
    }

    /**
     * nodeChoice -> ( LimitClause() ( OffsetClause() )? | OffsetClause() (
     * LimitClause() )? )
     */
    @Override
    public R visit(LimitOffsetClauses n) {
        R _ret = null;
        n.nodeChoice.accept(this);
        return _ret;
    }

    /**
     * nodeToken -> <ORDER> nodeToken1 -> <BY> nodeList -> ( OrderCondition() )+
     */
    @Override
    public R visit(OrderClause n) {
        R _ret = null;
        n.nodeToken.accept(this);
        n.nodeToken1.accept(this);
        n.nodeList.accept(this);
        return _ret;
    }

    /**
     * nodeChoice -> ( ( <ASC> | <DESC> ) BrackettedExpression() ) | (
     * Constraint() | Var() )
     */
    @Override
    public R visit(OrderCondition n) {
        R _ret = null;
        n.nodeChoice.accept(this);
        return _ret;
    }

    /**
     * nodeToken -> <LIMIT> nodeToken1 -> <INTEGER>
     */
    @Override
    public R visit(LimitClause n) {
        R _ret = null;
        n.nodeToken.accept(this);
        n.nodeToken1.accept(this);
        return _ret;
    }

    /**
     * nodeToken -> <OFFSET> nodeToken1 -> <INTEGER>
     */
    @Override
    public R visit(OffsetClause n) {
        R _ret = null;
        n.nodeToken.accept(this);
        n.nodeToken1.accept(this);
        return _ret;
    }

    /**
     * nodeToken -> "{" nodeOptional -> ( TriplesBlock() )? nodeListOptional ->
     * ( GraphPatternNotTriples() ( "." )? ( TriplesBlock() )? )* nodeToken1 ->
     * "}"
     */
    @Override
    public R visit(GroupGraphPattern n) {
        R _ret = null;
        n.nodeToken.accept(this);
        n.nodeOptional.accept(this);
        n.nodeListOptional.accept(this);
        n.nodeToken1.accept(this);
        return _ret;
    }

    /**
     * triplesSameSubject -> TriplesSameSubject() nodeOptional -> ( "." (
     * TriplesBlock() )? )?
     */
    @Override
    public R visit(TriplesBlock n) {
        R _ret = null;
        n.triplesSameSubject.accept(this);
        n.nodeOptional.accept(this);
        return _ret;
    }

    /**
     * nodeChoice -> OptionalGraphPattern() | GroupOrUnionGraphPattern() |
     * GraphGraphPattern() | Filter() | Bind()
     */
    @Override
    public R visit(GraphPatternNotTriples n) {
        R _ret = null;
        n.nodeChoice.accept(this);
        return _ret;
    }

    /**
     * nodeToken -> <OPTIONAL> groupGraphPattern -> GroupGraphPattern()
     */
    @Override
    public R visit(OptionalGraphPattern n) {
        R _ret = null;
        n.nodeToken.accept(this);
        n.groupGraphPattern.accept(this);
        return _ret;
    }

    /**
     * nodeToken -> <GRAPH> varOrIRIref -> VarOrIRIref() groupGraphPattern ->
     * GroupGraphPattern()
     */
    @Override
    public R visit(GraphGraphPattern n) {
        R _ret = null;
        n.nodeToken.accept(this);
        n.varOrIRIref.accept(this);
        n.groupGraphPattern.accept(this);
        return _ret;
    }

    /**
     * groupGraphPattern -> GroupGraphPattern() nodeListOptional -> ( <UNION>
     * GroupGraphPattern() )*
     */
    @Override
    public R visit(GroupOrUnionGraphPattern n) {
        R _ret = null;
        n.groupGraphPattern.accept(this);
        n.nodeListOptional.accept(this);
        return _ret;
    }

    /**
     * nodeToken -> <FILTER> constraint -> Constraint()
     */
    @Override
    public R visit(Filter n) {
        R _ret = null;
        n.nodeToken.accept(this);
        n.constraint.accept(this);
        return _ret;
    }

    /**
     * nodeChoice -> BrackettedExpression() | BuiltInCall() | FunctionCall()
     */
    @Override
    public R visit(Constraint n) {
        R _ret = null;
        n.nodeChoice.accept(this);
        return _ret;
    }

    /**
     * iRIref -> IRIref() argList -> ArgList()
     */
    @Override
    public R visit(FunctionCall n) {
        R _ret = null;
        n.iRIref.accept(this);
        n.argList.accept(this);
        return _ret;
    }

    /**
     * nodeChoice -> ( <NIL> | "(" Expression() ( "," Expression() )* ")" )
     */
    @Override
    public R visit(ArgList n) {
        R _ret = null;
        n.nodeChoice.accept(this);
        return _ret;
    }

    /**
     * nodeToken -> "{" nodeOptional -> ( ConstructTriples() )? nodeToken1 ->
     * "}"
     */
    @Override
    public R visit(ConstructTemplate n) {
        R _ret = null;
        n.nodeToken.accept(this);
        n.nodeOptional.accept(this);
        n.nodeToken1.accept(this);
        return _ret;
    }

    /**
     * triplesSameSubject -> TriplesSameSubject() nodeOptional -> ( "." (
     * ConstructTriples() )? )?
     */
    @Override
    public R visit(ConstructTriples n) {
        R _ret = null;
        n.triplesSameSubject.accept(this);
        n.nodeOptional.accept(this);
        return _ret;
    }

    /**
     * nodeChoice -> VarOrTerm() PropertyListNotEmpty() | TriplesNode()
     * PropertyList()
     */
    @Override
    public R visit(TriplesSameSubject n) {
        R _ret = null;
        n.nodeChoice.accept(this);
        return _ret;
    }

    /**
     * verb -> Verb() objectList -> ObjectList() nodeListOptional -> ( ";" (
     * Verb() ObjectList() )? )*
     */
    @Override
    public R visit(PropertyListNotEmpty n) {
        R _ret = null;
        n.verb.accept(this);
        n.objectList.accept(this);
        n.nodeListOptional.accept(this);
        return _ret;
    }

    /**
     * nodeOptional -> ( PropertyListNotEmpty() )?
     */
    @Override
    public R visit(PropertyList n) {
        R _ret = null;
        n.nodeOptional.accept(this);
        return _ret;
    }

    /**
     * sparqlObject -> SparqlObject() nodeListOptional -> ( "," SparqlObject()
     * )*
     */
    @Override
    public R visit(ObjectList n) {
        R _ret = null;
        n.sparqlObject.accept(this);
        n.nodeListOptional.accept(this);
        return _ret;
    }

    /**
     * graphNode -> GraphNode()
     */
    @Override
    public R visit(SparqlObject n) {
        R _ret = null;
        n.graphNode.accept(this);
        return _ret;
    }

    /**
     * nodeChoice -> VarOrIRIref() | <TYPE_A> | PutativeLiteral()
     */
    @Override
    public R visit(Verb n) {
        R _ret = null;
        n.nodeChoice.accept(this);
        return _ret;
    }

    /**
     * nodeChoice -> SparqlCollection() | BlankNodePropertyList()
     */
    @Override
    public R visit(TriplesNode n) {
        R _ret = null;
        n.nodeChoice.accept(this);
        return _ret;
    }

    /**
     * nodeToken -> "[" propertyListNotEmpty -> PropertyListNotEmpty()
     * nodeToken1 -> "]"
     */
    @Override
    public R visit(BlankNodePropertyList n) {
        R _ret = null;
        n.nodeToken.accept(this);
        n.propertyListNotEmpty.accept(this);
        n.nodeToken1.accept(this);
        return _ret;
    }

    /**
     * nodeToken -> "(" nodeList -> ( GraphNode() )+ nodeToken1 -> ")"
     */
    @Override
    public R visit(SparqlCollection n) {
        R _ret = null;
        n.nodeToken.accept(this);
        n.nodeList.accept(this);
        n.nodeToken1.accept(this);
        return _ret;
    }

    /**
     * nodeChoice -> VarOrTerm() | TriplesNode()
     */
    @Override
    public R visit(GraphNode n) {
        R _ret = null;
        n.nodeChoice.accept(this);
        return _ret;
    }

    /**
     * nodeChoice -> Var() | GraphTerm()
     */
    @Override
    public R visit(VarOrTerm n) {
        R _ret = null;
        n.nodeChoice.accept(this);
        return _ret;
    }

    /**
     * nodeChoice -> Var() | IRIref()
     */
    @Override
    public R visit(VarOrIRIref n) {
        R _ret = null;
        n.nodeChoice.accept(this);
        return _ret;
    }

    /**
     * nodeChoice -> <VAR1> | <VAR2>
     */
    @Override
    public R visit(Var n) {
        R _ret = null;
        n.nodeChoice.accept(this);
        return _ret;
    }

    /**
     * nodeChoice -> IRIref() | RDFLiteral() | NumericLiteral() |
     * BooleanLiteral() | BlankNode() | PutativeLiteral() | <NIL>
     */
    @Override
    public R visit(GraphTerm n) {
        R _ret = null;
        n.nodeChoice.accept(this);
        return _ret;
    }

    /**
     * conditionalOrExpression -> ConditionalOrExpression()
     */
    @Override
    public R visit(Expression n) {
        R _ret = null;
        n.conditionalOrExpression.accept(this);
        return _ret;
    }

    /**
     * conditionalAndExpression -> ConditionalAndExpression() nodeListOptional
     * -> ( "||" ConditionalAndExpression() )*
     */
    @Override
    public R visit(ConditionalOrExpression n) {
        R _ret = null;
        n.conditionalAndExpression.accept(this);
        n.nodeListOptional.accept(this);
        return _ret;
    }

    /**
     * valueLogical -> ValueLogical() nodeListOptional -> ( "&&" ValueLogical()
     * )*
     */
    @Override
    public R visit(ConditionalAndExpression n) {
        R _ret = null;
        n.valueLogical.accept(this);
        n.nodeListOptional.accept(this);
        return _ret;
    }

    /**
     * relationalExpression -> RelationalExpression()
     */
    @Override
    public R visit(ValueLogical n) {
        R _ret = null;
        n.relationalExpression.accept(this);
        return _ret;
    }

/**
     * numericExpression -> NumericExpression() nodeOptional -> ( "="
     * NumericExpression() | "!=" NumericExpression() | "<" NumericExpression()
     * | ">" NumericExpression() | "<=" NumericExpression() | ">="
     * NumericExpression() )?
     */
    @Override
    public R visit(RelationalExpression n) {
        R _ret = null;
        n.numericExpression.accept(this);
        n.nodeOptional.accept(this);
        return _ret;
    }

    /**
     * additiveExpression -> AdditiveExpression()
     */
    @Override
    public R visit(NumericExpression n) {
        R _ret = null;
        n.additiveExpression.accept(this);
        return _ret;
    }

    /**
     * multiplicativeExpression -> MultiplicativeExpression() nodeListOptional
     * -> ( "+" MultiplicativeExpression() | "-" MultiplicativeExpression() |
     * NumericLiteralPositive() | NumericLiteralNegative() )*
     */
    @Override
    public R visit(AdditiveExpression n) {
        R _ret = null;
        n.multiplicativeExpression.accept(this);
        n.nodeListOptional.accept(this);
        return _ret;
    }

    /**
     * unaryExpression -> UnaryExpression() nodeListOptional -> ( "*"
     * UnaryExpression() | "/" UnaryExpression() )*
     */
    @Override
    public R visit(MultiplicativeExpression n) {
        R _ret = null;
        n.unaryExpression.accept(this);
        n.nodeListOptional.accept(this);
        return _ret;
    }

    /**
     * nodeChoice -> ( "!" PrimaryExpression() | "+" PrimaryExpression() | "-"
     * PrimaryExpression() | PrimaryExpression() )
     */
    @Override
    public R visit(UnaryExpression n) {
        R _ret = null;
        n.nodeChoice.accept(this);
        return _ret;
    }

    /**
     * nodeChoice -> BrackettedExpression() | BuiltInCall() | IRIrefOrFunction()
     * | RDFLiteral() | NumericLiteral() | BooleanLiteral() | Var()
     */
    @Override
    public R visit(PrimaryExpression n) {
        R _ret = null;
        n.nodeChoice.accept(this);
        return _ret;
    }

    /**
     * nodeToken -> "(" expression -> Expression() nodeToken1 -> ")"
     */
    @Override
    public R visit(BrackettedExpression n) {
        R _ret = null;
        n.nodeToken.accept(this);
        n.expression.accept(this);
        n.nodeToken1.accept(this);
        return _ret;
    }

    /**
     * nodeChoice -> ( <STR> "(" Expression() ")" | <LANG> "(" Expression() ")"
     * | <LANGMATCHES> "(" Expression() "," Expression() ")" | <DATATYPE> "("
     * Expression() ")" | <BOUND> "(" Var() ")" | <SAME_TERM> "(" Expression()
     * "," Expression() ")" | <IS_IRI> "(" Expression() ")" | <IS_URI> "("
     * Expression() ")" | <IS_BLANK> "(" Expression() ")" | <IS_LITERAL> "("
     * Expression() ")" | RegexExpression() )
     */
    @Override
    public R visit(BuiltInCall n) {
        R _ret = null;
        n.nodeChoice.accept(this);
        return _ret;
    }

    /**
     * nodeToken -> <REGEX> nodeToken1 -> "(" expression -> Expression()
     * nodeToken2 -> "," expression1 -> Expression() nodeOptional -> ( ","
     * Expression() )? nodeToken3 -> ")"
     */
    @Override
    public R visit(RegexExpression n) {
        R _ret = null;
        n.nodeToken.accept(this);
        n.nodeToken1.accept(this);
        n.expression.accept(this);
        n.nodeToken2.accept(this);
        n.expression1.accept(this);
        n.nodeOptional.accept(this);
        n.nodeToken3.accept(this);
        return _ret;
    }

    /**
     * iRIref -> IRIref() nodeOptional -> ( ArgList() )?
     */
    @Override
    public R visit(IRIrefOrFunction n) {
        R _ret = null;
        n.iRIref.accept(this);
        n.nodeOptional.accept(this);
        return _ret;
    }

    /**
     * sparqlString -> SparqlString() nodeOptional -> ( <LANGTAG> | ( "^^"
     * IRIref() ) )?
     */
    @Override
    public R visit(RDFLiteral n) {
        R _ret = null;
        n.sparqlString.accept(this);
        n.nodeOptional.accept(this);
        return _ret;
    }

    /**
     * nodeChoice -> ( NumericLiteralUnsigned() | NumericLiteralPositive() |
     * NumericLiteralNegative() )
     */
    @Override
    public R visit(NumericLiteral n) {
        R _ret = null;
        n.nodeChoice.accept(this);
        return _ret;
    }

    /**
     * nodeChoice -> <INTEGER> | <DECIMAL> | <DOUBLE>
     */
    @Override
    public R visit(NumericLiteralUnsigned n) {
        R _ret = null;
        n.nodeChoice.accept(this);
        return _ret;
    }

    /**
     * nodeChoice -> <INTEGER_POSITIVE> | <DECIMAL_POSITIVE> | <DOUBLE_POSITIVE>
     */
    @Override
    public R visit(NumericLiteralPositive n) {
        R _ret = null;
        n.nodeChoice.accept(this);
        return _ret;
    }

    /**
     * nodeChoice -> <INTEGER_NEGATIVE> | <DECIMAL_NEGATIVE> | <DOUBLE_NEGATIVE>
     */
    @Override
    public R visit(NumericLiteralNegative n) {
        R _ret = null;
        n.nodeChoice.accept(this);
        return _ret;
    }

    /**
     * nodeChoice -> <TRUE> | <FALSE>
     */
    @Override
    public R visit(BooleanLiteral n) {
        R _ret = null;
        n.nodeChoice.accept(this);
        return _ret;
    }

    /**
     * nodeChoice -> <STRING_LITERAL1> | <STRING_LITERAL2> |
     * <STRING_LITERAL_LONG1> | <STRING_LITERAL_LONG2>
     */
    @Override
    public R visit(SparqlString n) {
        R _ret = null;
        n.nodeChoice.accept(this);
        return _ret;
    }

    /**
     * nodeChoice -> <IRI_REF> | PrefixedName()
     */
    @Override
    public R visit(IRIref n) {
        R _ret = null;
        n.nodeChoice.accept(this);
        return _ret;
    }

    /**
     * nodeChoice -> <PNAME_LN> | <PNAME_NS>
     */
    @Override
    public R visit(PrefixedName n) {
        R _ret = null;
        n.nodeChoice.accept(this);
        return _ret;
    }

    /**
     * nodeChoice -> <BLANK_NODE_LABEL> | <ANON>
     */
    @Override
    public R visit(BlankNode n) {
        R _ret = null;
        n.nodeChoice.accept(this);
        return _ret;
    }

    /**
     * nodeToken -> <PUTATIVE_LITERAL>
     */
    @Override
    public R visit(PutativeLiteral n) {
        R _ret = null;
        n.nodeToken.accept(this);
        return _ret;
    }

    /**
     * prologue -> Prologue() nodeList -> ( "[" Rule() "]" )+
     */
    @Override
    public R visit(SparqlSpin n) {
        R _ret = null;
        n.prologue.accept(this);
        n.nodeList.accept(this);
        return _ret;
    }

    /**
     * consequent -> Consequent() nodeToken -> <WHERE> antecedent ->
     * Antecedent()
     */
    @Override
    public R visit(Rule n) {
        R _ret = null;
        n.consequent.accept(this);
        n.nodeToken.accept(this);
        n.antecedent.accept(this);
        return _ret;
    }

    /**
     * nodeChoice -> RuleConstructClause() | RuleModifyClause()
     */
    @Override
    public R visit(Consequent n) {
        R _ret = null;
        n.nodeChoice.accept(this);
        return _ret;
    }

    /**
     * nodeToken -> <CONSTRUCT> constructTemplate -> ConstructTemplate()
     * nodeListOptional -> ( DatasetClause() )*
     */
    @Override
    public R visit(RuleConstructClause n) {
        R _ret = null;
        n.nodeToken.accept(this);
        n.constructTemplate.accept(this);
        n.nodeListOptional.accept(this);
        return _ret;
    }

    /**
     * nodeToken -> <DELETE> deleteClause -> DeleteClause() nodeOptional -> (
     * <INSERT> InsertClause() )?
     */
    @Override
    public R visit(RuleModifyClause n) {
        R _ret = null;
        n.nodeToken.accept(this);
        n.deleteClause.accept(this);
        n.nodeOptional.accept(this);
        return _ret;
    }

    /**
     * nodeToken -> "{" nodeOptional -> ( DeleteTriples() )? nodeToken1 -> "}"
     */
    @Override
    public R visit(DeleteClause n) {
        R _ret = null;
        n.nodeToken.accept(this);
        n.nodeOptional.accept(this);
        n.nodeToken1.accept(this);
        return _ret;
    }

    /**
     * triplesSameSubject -> TriplesSameSubject() nodeOptional -> ( "." (
     * DeleteTriples() )? )?
     */
    @Override
    public R visit(DeleteTriples n) {
        R _ret = null;
        n.triplesSameSubject.accept(this);
        n.nodeOptional.accept(this);
        return _ret;
    }

    /**
     * nodeToken -> "{" nodeOptional -> ( InsertTriples() )? nodeToken1 -> "}"
     */
    @Override
    public R visit(InsertClause n) {
        R _ret = null;
        n.nodeToken.accept(this);
        n.nodeOptional.accept(this);
        n.nodeToken1.accept(this);
        return _ret;
    }

    /**
     * triplesSameSubject -> TriplesSameSubject() nodeOptional -> ( "." (
     * InsertTriples() )? )?
     */
    @Override
    public R visit(InsertTriples n) {
        R _ret = null;
        n.triplesSameSubject.accept(this);
        n.nodeOptional.accept(this);
        return _ret;
    }

    /**
     * whereClause -> WhereClause() solutionModifier -> SolutionModifier()
     */
    @Override
    public R visit(Antecedent n) {
        R _ret = null;
        n.whereClause.accept(this);
        n.solutionModifier.accept(this);
        return _ret;
    }

    /**
     * nodeToken -> <BIND> nodeToken1 -> "(" expression -> Expression()
     * nodeToken2 -> <AS> var -> Var() nodeToken3 -> ")"
     */
    @Override
    public R visit(Bind n) {
        R _ret = null;
        n.nodeToken.accept(this);
        n.nodeToken1.accept(this);
        n.expression.accept(this);
        n.nodeToken2.accept(this);
        n.var.accept(this);
        n.nodeToken3.accept(this);
        return _ret;
    }

}
