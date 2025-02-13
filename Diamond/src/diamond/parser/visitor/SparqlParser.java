package diamond.parser.visitor;

import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Stack;

import diamond.bookkeeping.ExpressionTreeNode;
import diamond.bookkeeping.GraphPatternType;
import diamond.bookkeeping.PrefixLibrary;
import diamond.builtincall.Bound;
import diamond.builtincall.Datatype;
import diamond.builtincall.IsIRI;
import diamond.builtincall.IsLiteral;
import diamond.builtincall.Lang;
import diamond.builtincall.LangMatches;
import diamond.builtincall.Regex;
import diamond.builtincall.STR;
import diamond.data.DataType;
import diamond.data.Element;
import diamond.data.ExprNode;
import diamond.data.SPO;
import diamond.data.TriplePattern;
import diamond.processors.URLProcessor;
import diamond.retenetwork.ReteNetwork;
import diamond.spin.SpinConstruct;
import diamond.spin.SpinModify;
import diamond.parser.syntaxtree.*;

/**
 * Used to visit SPARQL parse tree nodes.
 * 
 * @author Rodolfo Kaplan Depena (but really a modification of a generated JTB visitor class suited for my own purposes)
 * @author Slavcho Slavchev
 */
public class SparqlParser implements GJNoArguVisitor<Element> {

    private URLProcessor urlProcessor;
    private TriplePattern triplePattern;
    private List<TriplePattern> triplePatterns;
    private PrefixLibrary prefixLibrary;
    private List<URL> urls;
    private ReteNetwork reteNetwork;
    private List<ReteNetwork> spinReteNetworks;
    private List<java.lang.String> selectList;
    private boolean allVarsAreFromSelectList;
    private boolean isAsteriskInSelectList;
    private java.lang.String doubleCarrot;
    private java.lang.String atString;
    private ExpressionTreeNode exprTreeRoot;
    
    // instance variables for filter and negation
    private boolean syntacticSugar;
    public boolean isOptimized;
    public ArrayList<LimitClause> limitMod;
    public SpinConstruct construct;
    public SpinModify modify;
    public boolean isConstruct = false;

    // BuiltInCall
    private int insideBuiltInCall = 0;
    private Stack<ExprNode> builtInCallStack = new Stack<ExprNode>();
    
    /**
     * Create visitor that traverses a SPARQL syntax tree in order to assist in
     * parsing SPARQL queries.
     */
    public SparqlParser(boolean debug) {
        urlProcessor = new URLProcessor();
        triplePatterns = new ArrayList<TriplePattern>();
        spinReteNetworks = new ArrayList<ReteNetwork>();
        prefixLibrary = new PrefixLibrary();
        urls = new ArrayList<URL>();
        selectList = new ArrayList<java.lang.String>();
        allVarsAreFromSelectList = false;
        isAsteriskInSelectList = false;
        doubleCarrot = "";
        atString = "";
        syntacticSugar = false;
        exprTreeRoot = null;
        isOptimized = true;// false;
        limitMod = new ArrayList<LimitClause>();
    }

    /**
     * Returns a list of variables from the select block of a 'SELECT QUERY'.
     */
    public List<java.lang.String> returnSelectList() {
        return selectList;
    }

    /**
     * Return a list of harvested URLs.
     */
    public List<URL> getURLs() throws Exception {
        return urls;
    }

    /**
     * Return a compiled Rete-network.
     */
    public ReteNetwork getReteNetwork() {
        return reteNetwork;
    }

    public List<ReteNetwork> getSpinReteNetworks() {
        return spinReteNetworks;
    }

    private void filterPrefixesInElement(Element e) {
        java.lang.String data = e.getData();
        boolean changed = false;
        for (java.lang.String prefix : prefixLibrary.keySet()) {
            java.lang.String sub = prefixLibrary.get(prefix);
            if (sub.charAt(0) == '<' && sub.charAt(sub.length() - 1) == '>') {
                sub = sub.substring(1, sub.length() - 1);
            }
            if (data.contains(prefix) && e.getDataType() == DataType.URL && changed == false) {
                if (data.charAt(0) == ':') {
                    e.setData(data.replace(prefix, sub));
                    changed = true;
                } else {
                    e.setData("<" + data.replace(prefix, sub) + ">");
                    changed = true;
                }
            }
        }
    }

    /**
     * Format the URLs within a given triple pattern
     */
    private TriplePattern formatURLsInTriplePattern(TriplePattern tp) {
        if (tp.getSubject().getDataType() == DataType.URL) {
            Element subj = tp.getSubject();
            java.lang.String dataFromSubject = subj.getData();
            subj.setData(formatURL(dataFromSubject));
            tp.setSubject(subj);
        }
        if (tp.getPredicate().getDataType() == DataType.URL) {
            Element pred = tp.getPredicate();
            java.lang.String dataFromPredicate = pred.getData();
            pred.setData(formatURL(dataFromPredicate));
            tp.setPredicate(pred);
        }
        if (tp.getObject().getDataType() == DataType.URL) {
            Element obj = tp.getObject();
            java.lang.String dataFromObject = obj.getData();
            obj.setData(formatURL(dataFromObject));
            tp.setObject(obj);
        }
        return tp;
    }

    /**
     * Format a URL string by taking off the angled brackets "<" and ">"
     * 
     * @param url
     * @return a formatted url string
     */
    private java.lang.String formatURL(java.lang.String url) {
        if (url.charAt(0) == '<' && url.charAt(url.length() - 1) == '>') {
            url = url.replace("<", "");
            url = url.replace(">", "");
        }
        return url;
    }

    //
    // Auto class visitors--probably don't need to be overridden.
    //

    @Override
    public Element visit(Antecedent n) {
        n.whereClause.accept(this);
        n.solutionModifier.accept(this);
        if (isConstruct)
            construct.setMods(limitMod);
        else
            modify.setMods(limitMod);
        return null;
    }

    @Override
    public Element visit(Consequent n) {
        n.nodeChoice.accept(this);
        return null;
    }

    @Override
    public Element visit(RuleConstructClause n) {
        isConstruct = true;
        construct = new SpinConstruct();
        construct.setMods(limitMod);
        reteNetwork = new ReteNetwork();
        exprTreeRoot = new ExpressionTreeNode(GraphPatternType.GROUP_GRAPH_PATTERN);
        reteNetwork.setExpressionTreeNode(exprTreeRoot);
        n.nodeToken.accept(this);
        n.constructTemplate.accept(this);
        n.nodeListOptional.accept(this);
        reteNetwork.setSpinRule(construct);
        ((SpinConstruct) reteNetwork.getSpinRule()).setConstructPatterns(triplePatterns);
        triplePatterns.clear();
        limitMod.clear();
        return null;
    }

    @Override
    public Element visit(RuleModifyClause n) {
        isConstruct = false;
        modify = new SpinModify();
        modify.setMods(limitMod);
        reteNetwork = new ReteNetwork();
        exprTreeRoot = new ExpressionTreeNode(GraphPatternType.GROUP_GRAPH_PATTERN);
        reteNetwork.setExpressionTreeNode(exprTreeRoot);
        reteNetwork.setSpinRule(modify);
        n.nodeToken.accept(this);
        n.deleteClause.accept(this);
        n.nodeOptional.accept(this);
        triplePatterns.clear();
        limitMod.clear();
        return null;
    }

    @Override
    public Element visit(SparqlSpin n) {
        n.prologue.accept(this);
        for (Node e : n.nodeList.nodes) {
            e.accept(this);
        }
        return null;
    }

    @Override
    public Element visit(Rule n) {
        n.consequent.accept(this);
        n.nodeToken.accept(this);
        n.antecedent.accept(this);
        spinReteNetworks.add(reteNetwork);
        return null;
    }

    @Override
    public Element visit(NodeList n) {
        for (Enumeration<Node> e = n.elements(); e.hasMoreElements();)
            e.nextElement().accept(this);
        return null;
    }

    /**
     * @param n - NodeListOptional
     * @return null - Element
     */
    @Override
    public Element visit(NodeListOptional n) {
        Element nodeListOpt = null;
        if (n.present())
            for (Enumeration<Node> e = n.elements(); e.hasMoreElements();)
                nodeListOpt = e.nextElement().accept(this);
        return nodeListOpt;
    }

    /**
     * @param n - NodeOptional
     * @return null - Element
     */
    @Override
    public Element visit(NodeOptional n) {
        Element nodeOpt = null;
        if (n.present())
            nodeOpt = n.node.accept(this);
        return nodeOpt;
    }

    /**
     * @param n - NodeSequence
     * @return null - Element
     */
    @Override
    public Element visit(NodeSequence n) {
        Element nodeSeq = null;
        for (Enumeration<Node> e = n.elements(); e.hasMoreElements();)
            nodeSeq = e.nextElement().accept(this);
        return nodeSeq;
    }

    /**
     * Visit NodeToken node
     * 
     * @param n - NodeToken
     * @return nodeToken - Element
     */
    @Override
    public Element visit(NodeToken n) {
        java.lang.String nodeTok = n.tokenImage;
        if (syntacticSugar) {
            if (nodeTok.equals(";")) {
                triplePatterns.add(triplePattern);
                exprTreeRoot.addTriplePattern(triplePattern);
                try {
                    urls.addAll(urlProcessor.extractURLsFromTriplePattern(triplePattern));
                } catch (Exception e) {
                    System.err.print("Failed to extract URLs from TriplePattern");
                }
                triplePattern = new TriplePattern();
                triplePattern.setSubject(triplePatterns.get(triplePatterns.size() - 1).getSubject());
            }
        }

        if (nodeTok.equals("^^")) {
            doubleCarrot = "^^";
        }

        if (nodeTok.equals("@")) {
            atString = "@";
        }

        Element nodeToken = new Element(null, null, nodeTok);
        return nodeToken;
    }

    //
    // User-generated visitor methods below
    //

    /**
     * <PRE>
     * prologue -> Prologue()
     * nodeChoice -> ( SelectQuery() | ConstructQuery() | DescribeQuery() | AskQuery() )
     * </PRE>
     */
    @Override
    public Element visit(Query n) {
        n.prologue.accept(this);
        n.nodeChoice.accept(this);
        return null;
    }

    /**
     * <PRE>
     * nodeOptional -> ( BaseDecl() )?
     * nodeListOptional -> ( PrefixDecl() )*
     * </PRE>
     */
    @Override
    public Element visit(Prologue n) {
        n.nodeOptional.accept(this);
        n.nodeListOptional.accept(this);
        return null;
    }

    /**
     * <PRE>
     * nodeToken -> &lt;BASE&gt;
     * nodeToken1 -> &lt;IRI_REF&gt;
     * </PRE>
     */
    @Override
    public Element visit(BaseDecl n) {
        n.nodeToken.accept(this);
        n.nodeToken1.accept(this);
        return null;
    }

    /**
     * <PRE>
     * nodeToken -> &lt;PREFIX&gt;
     * nodeToken1 -> &lt;PNAME_NS&gt;
     * nodeToken2 -> &lt;IRI_REF&gt;
     * </PRE>
     */
    @Override
    public Element visit(PrefixDecl n) {
        n.nodeToken.accept(this);
        n.nodeToken1.accept(this);
        java.lang.String prefix = n.nodeToken1.tokenImage;
        n.nodeToken2.accept(this);
        java.lang.String urlAssignedToPrefix = n.nodeToken2.tokenImage;
        prefixLibrary.put(prefix, urlAssignedToPrefix);
        return null;
    }

    /**
     * <PRE>
     * nodeToken -> &lt;SELECT&gt;
     * nodeOptional -> ( &lt;DISTINCT&gt; | &lt;REDUCED&gt; )?
     * nodeChoice -> ( ( Var() )+ | "*" )
     * nodeListOptional -> ( DatasetClause() )*
     * whereClause -> WhereClause()
     * solutionModifier -> SolutionModifier()
     * </PRE>
     */
    @Override
    public Element visit(SelectQuery n) {
        n.nodeToken.accept(this);
        n.nodeOptional.accept(this);
        allVarsAreFromSelectList = true;
        n.nodeChoice.accept(this);
        allVarsAreFromSelectList = false;
        if (selectList.size() == 0) {
            isAsteriskInSelectList = true;
        }
        reteNetwork = new ReteNetwork(selectList);
        n.nodeListOptional.accept(this);
        n.whereClause.accept(this);
        n.solutionModifier.accept(this);
        return null;
    }

    /**
     * <PRE>
     * nodeToken -> &lt;CONSTRUCT&gt;
     * constructTemplate -> ConstructTemplate()
     * nodeListOptional -> ( DatasetClause() )*
     * whereClause -> WhereClause()
     * solutionModifier -> SolutionModifier()
     * </PRE>
     */
    @Override
    public Element visit(ConstructQuery n) {
        n.nodeToken.accept(this);
        n.constructTemplate.accept(this);
        n.nodeListOptional.accept(this);
        n.whereClause.accept(this);
        n.solutionModifier.accept(this);
        return null;
    }

    /**
     * <PRE>
     * nodeToken -> &lt;DESCRIBE&gt;
     * nodeChoice -> ( ( VarOrIRIref() )+ | "*" )
     * nodeListOptional -> ( DatasetClause() )*
     * nodeOptional -> ( WhereClause() )?
     * solutionModifier -> SolutionModifier()
     * </PRE>
     */
    @Override
    public Element visit(DescribeQuery n) {
        n.nodeToken.accept(this);
        n.nodeChoice.accept(this);
        n.nodeListOptional.accept(this);
        n.nodeOptional.accept(this);
        n.solutionModifier.accept(this);
        return null;
    }

    /**
     * <PRE>
     * nodeToken -> &lt;ASK&gt;
     * nodeListOptional -> ( DatasetClause() )*
     * whereClause -> WhereClause()
     * </PRE>
     */
    @Override
    public Element visit(AskQuery n) {
        n.nodeToken.accept(this);
        n.nodeListOptional.accept(this);
        n.whereClause.accept(this);
        return null;
    }

    /**
     * <PRE>
     * nodeToken -> &lt;FROM&gt;
     * nodeChoice -> ( DefaultGraphClause() | NamedGraphClause() )
     * </PRE>
     */
    @Override
    public Element visit(DatasetClause n) {
        n.nodeToken.accept(this);
        n.nodeChoice.accept(this);
        return null;
    }

    /**
     * <PRE>
     * sourceSelector -> SourceSelector()
     * </PRE>
     */
    @Override
    public Element visit(DefaultGraphClause n) {
        n.sourceSelector.accept(this);
        return null;
    }

    /**
     * <PRE>
     * nodeToken -> &lt;NAMED&gt;
     * sourceSelector -> SourceSelector()
     * </PRE>
     */
    @Override
    public Element visit(NamedGraphClause n) {
        n.nodeToken.accept(this);
        n.sourceSelector.accept(this);
        return null;
    }

    /**
     * <PRE>
     * iRIref -> IRIref()
     * </PRE>
     */
    @Override
    public Element visit(SourceSelector n) {
        n.iRIref.accept(this);
        return null;
    }

    /**
     * <PRE>
     * nodeOptional -> ( &lt;WHERE&gt; )?
     * groupGraphPattern -> GroupGraphPattern()
     * </PRE>
     */
    @Override
    public Element visit(WhereClause n) {
        exprTreeRoot = new ExpressionTreeNode(GraphPatternType.GROUP_GRAPH_PATTERN);
        reteNetwork.setExpressionTreeNode(exprTreeRoot);
        n.nodeOptional.accept(this);
        Element groupGraphPattern = n.groupGraphPattern.accept(this);
        exprTreeRoot = null;
        return groupGraphPattern;
    }

    /**
     * <PRE>
     * nodeOptional -> ( OrderClause() )?
     * nodeOptional1 -> ( LimitOffsetClauses() )?
     * </PRE>
     */
    @Override
    public Element visit(SolutionModifier n) {
        n.nodeOptional.accept(this); // potentially an order by
        n.nodeOptional1.accept(this); // potentially limit/offset
        return null;
    }

    /**
     * <PRE>
     * nodeChoice -> ( LimitClause() ( OffsetClause() )? | OffsetClause() ( LimitClause() )? )
     * </PRE>
     */
    @Override
    public Element visit(LimitOffsetClauses n) {
        n.nodeChoice.accept(this);
        return null;
    }

    /**
     * <PRE>
     * nodeToken -> &lt;ORDER&gt;
     * nodeToken1 -> &lt;BY&gt;
     * nodeList -> ( OrderCondition() )+
     * </PRE>
     */
    @Override
    public Element visit(OrderClause n) {
        n.nodeToken.accept(this);
        n.nodeToken1.accept(this);
        n.nodeList.accept(this);
        // modifiers.add(n);
        return null;
    }

    /**
     * <PRE>
     * nodeChoice -> ( ( &lt;ASC&gt; | &lt;DESC&gt; ) BrackettedExpression() )
     *       | ( Constraint() | Var() )
     * </PRE>
     */
    @Override
    public Element visit(OrderCondition n) {
        n.nodeChoice.accept(this);
        return null;
    }

    /**
     * <PRE>
     * nodeToken -> &lt;LIMIT&gt;
     * nodeToken1 -> &lt;INTEGER&gt;
     * </PRE>
     */
    @Override
    public Element visit(LimitClause n) {
        n.nodeToken.accept(this);
        n.nodeToken1.accept(this);
        limitMod.add(n);
        return null;
    }

    /**
     * <PRE>
     * nodeToken -> &lt;OFFSET&gt;
     * nodeToken1 -> &lt;INTEGER&gt;
     * </PRE>
     */
    @Override
    public Element visit(OffsetClause n) {
        n.nodeToken.accept(this);
        n.nodeToken1.accept(this);
        // modifiers.add(n);
        return null;
    }

    /**
     * <PRE>
     * nodeToken -> "{"
     * nodeOptional -> ( TriplesBlock() )?
     * nodeListOptional -> ( ( GraphPatternNotTriples() | Filter() ) ( "." )? ( TriplesBlock() )? )*
     * nodeToken1 -> "}"
     * </PRE>
     */
    @Override
    public Element visit(GroupGraphPattern n) {
        ExpressionTreeNode parent = exprTreeRoot;
        exprTreeRoot = new ExpressionTreeNode(GraphPatternType.GROUP_GRAPH_PATTERN);
        parent.addChild(exprTreeRoot);
        n.nodeToken.accept(this);
        n.nodeOptional.accept(this);
        n.nodeListOptional.accept(this);
        n.nodeToken1.accept(this);
        exprTreeRoot = parent;
        return null;
    }

    /**
     * <PRE>
     * triplesSameSubject -> TriplesSameSubject()
     * nodeOptional -> ( "." ( TriplesBlock() )? )?
     * </PRE>
     */
    @Override
    public Element visit(TriplesBlock n) {
        Element triplesSameSubject = n.triplesSameSubject.accept(this);
        n.nodeOptional.accept(this);
        return triplesSameSubject;
    }

    /**
     * <PRE>
     * nodeChoice -> ( OptionalGraphPattern() | GroupOrUnionGraphPattern() | GraphGraphPattern() )
     * </PRE>
     */
    @Override
    public Element visit(GraphPatternNotTriples n) {
        n.nodeChoice.accept(this);
        return null;
    }

    /**
     * <PRE>
     * nodeToken -> &lt;OPTIONAL&gt;
     * groupGraphPattern -> GroupGraphPattern()
     * </PRE>
     */
    @Override
    public Element visit(OptionalGraphPattern n) {
        ExpressionTreeNode parent = exprTreeRoot;
        exprTreeRoot = new ExpressionTreeNode(GraphPatternType.OPTIONAL_GRAPH_PATTERN);
        parent.addChild(exprTreeRoot);
        n.nodeToken.accept(this);
        n.groupGraphPattern.accept(this);
        exprTreeRoot = parent;
        return null;
    }

    /**
     * <PRE>
     * nodeToken -> &lt;GRAPH&gt;
     * varOrIRIref -> VarOrIRIref()
     * groupGraphPattern -> GroupGraphPattern()
     * </PRE>
     */
    @Override
    public Element visit(GraphGraphPattern n) {
        ExpressionTreeNode parent = exprTreeRoot;
        exprTreeRoot = new ExpressionTreeNode(GraphPatternType.GRAPH_GRAPH_PATTERN);
        parent.addChild(exprTreeRoot);
        n.nodeToken.accept(this);
        n.varOrIRIref.accept(this);
        n.groupGraphPattern.accept(this);
        exprTreeRoot = parent;
        return null;
    }

    /**
     * <PRE>
     * groupGraphPattern -> GroupGraphPattern()
     * nodeListOptional -> ( &lt;UNION&gt; GroupGraphPattern() )*
     * </PRE>
     */
    @Override
    public Element visit(GroupOrUnionGraphPattern n) {
        ExpressionTreeNode parent = exprTreeRoot;
        if(!n.nodeListOptional.nodes.isEmpty()) { // Union
            exprTreeRoot = new ExpressionTreeNode(GraphPatternType.UNION_GRAPH_PATTERN);
            parent.addChild(exprTreeRoot);
        }
        n.groupGraphPattern.accept(this);
        n.nodeListOptional.accept(this);
        exprTreeRoot = parent;
        return null;
    }
    
    /**
     * <PRE>
     * nodeToken -> &lt;FILTER&gt;
     * constraint -> Constraint()
     * </PRE>
     */
    @Override
    public Element visit(Filter n) {
        ExprNode expr = (ExprNode) n.constraint.accept(this);
        diamond.retenetwork.RelationalExpression relExpr = new diamond.retenetwork.RelationalExpression(expr);
        exprTreeRoot.addFilter(new diamond.retenetwork.Filter(relExpr));
        return null;
    }
    
    /**
     * nodeToken -> <BIND> nodeToken1 -> "(" expression -> Expression()
     * nodeToken2 -> <AS> var -> Var() nodeToken3 -> ")"
     */
    @Override
    public Element visit(Bind n) {
        Element var = n.var.accept(this);
        ExprNode expr = (ExprNode) n.expression.accept(this);
        diamond.retenetwork.RelationalExpression relExpr = new diamond.retenetwork.RelationalExpression(expr);
        exprTreeRoot.addBind(new diamond.retenetwork.Bind(var.getData(), relExpr));
        return null;
    }

    /**
     * <PRE>
     * nodeChoice -> BrackettedExpression()
     *       | BuiltInCall()
     *       | FunctionCall()
     * </PRE>
     */
    @Override
    public Element visit(Constraint n) {
        Element expr = n.nodeChoice.accept(this);
        return expr;
    }

    /**
     * <PRE>
     * iRIref -> IRIref()
     * argList -> ArgList()
     * </PRE>
     */
    @Override
    public Element visit(FunctionCall n) {
        n.iRIref.accept(this);
        n.argList.accept(this);
        return null;
    }

    /**
     * <PRE>
     * nodeChoice -> ( &lt;NIL&gt; | "(" Expression() ( "," Expression() )* ")" )
     * </PRE>
     */
    @Override
    public Element visit(ArgList n) {
        n.nodeChoice.accept(this);
        return null;
    }

    /**
     * <PRE>
     * nodeToken -> "{"
     * nodeOptional -> ( ConstructTriples() )?
     * nodeToken1 -> "}"
     * </PRE>
     */
    @Override
    public Element visit(ConstructTemplate n) {
        n.nodeToken.accept(this);
        n.nodeOptional.accept(this);
        n.nodeToken1.accept(this);
        return null;
    }

    @Override
    public Element visit(InsertClause ic) {
        triplePatterns.clear();
        ic.nodeToken.accept(this);
        ic.nodeOptional.accept(this);
        ic.nodeToken1.accept(this);
        ((SpinModify) reteNetwork.getSpinRule()).setInsertPatterns(triplePatterns);
        triplePatterns.clear();
        return null;
    }

    @Override
    public Element visit(DeleteClause dc) {
        triplePatterns.clear();
        dc.nodeToken.accept(this);
        dc.nodeOptional.accept(this);
        dc.nodeToken1.accept(this);
        ((SpinModify) reteNetwork.getSpinRule()).setDeletePatterns(triplePatterns);
        triplePatterns.clear();
        return null;
    }

    /**
     * <PRE>
     * triplesSameSubject -> TriplesSameSubject()
     * nodeOptional -> ( "." ( ConstructTriples() )? )?
     * </PRE>
     */
    @Override
    public Element visit(ConstructTriples n) {
        n.triplesSameSubject.accept(this);
        n.nodeOptional.accept(this);
        return null;
    }

    // essentially construct
    @Override
    public Element visit(InsertTriples n) {
        n.triplesSameSubject.accept(this);
        n.nodeOptional.accept(this);
        return null;
    }

    // go through memory and remove triples
    @Override
    public Element visit(DeleteTriples n) {
        n.triplesSameSubject.accept(this);
        n.nodeOptional.accept(this);
        return null;
    }

    /**
     * <PRE>
     * nodeChoice -> VarOrTerm() PropertyListNotEmpty()
     *       | TriplesNode() PropertyList()
     * </PRE>
     */
    @Override
    public Element visit(TriplesSameSubject n) {
        triplePattern = new TriplePattern();
        Element elem = n.nodeChoice.accept(this);
        triplePattern = formatURLsInTriplePattern(triplePattern);
        triplePatterns.add(triplePattern);
        exprTreeRoot.addTriplePattern(triplePattern);
            try {
            urls.addAll(urlProcessor.extractURLsFromTriplePattern(triplePattern));
        } catch (Exception e) {
            System.err.print("Failed to extract URLs from TriplePattern");
        }
        triplePattern = null;
        return elem;
    }

    /**
     * <PRE>
     * verb -> Verb()
     * objectList -> ObjectList()
     * nodeListOptional -> ( ";" ( Verb() ObjectList() )? )*
     * </PRE>
     */
    @Override
    public Element visit(PropertyListNotEmpty n) {
        Element verb = n.verb.accept(this);
        n.objectList.accept(this);
        syntacticSugar = true;
        n.nodeListOptional.accept(this);
        syntacticSugar = false;
        return verb;
    }

    /**
     * <PRE>
     * nodeOptional -> ( PropertyListNotEmpty() )?
     * </PRE>
     */
    @Override
    public Element visit(PropertyList n) {
        Element propListNotEmpty = n.nodeOptional.accept(this);
        return propListNotEmpty;
    }

    /**
     * <PRE>
     * object -> Object()
     * nodeListOptional -> ( "," Object() )*
     * </PRE>
     */
    @Override
    public Element visit(ObjectList n) {
        Element obj = n.sparqlObject.accept(this);
        n.nodeListOptional.accept(this);
        return obj;
    }

    /**
     * <PRE>
     * nodeChoice -> VarOrIRIref()
     *       | &lt;TYPE_A&gt;
     *       | PutativeLiteral()
     * </PRE>
     */
    @Override
    public Element visit(Verb n) {
        Element verb = n.nodeChoice.accept(this);
        SPO verbSPO = verb.getSpo();
        if (verbSPO != SPO.OBJECT && verbSPO != SPO.SUBJECT) {
            verb.setSpo(SPO.PREDICATE);
            triplePattern.setPredicate(verb);
        }
        return verb;
    }
    
    /**
     * <PRE>
     * graphNode -> GraphNode()
     * </PRE>
     */
    @Override
    public Element visit(SparqlObject n) {
        Element graphNode = n.graphNode.accept(this);
        if (graphNode != null) {
            graphNode.setSpo(SPO.OBJECT);
            triplePattern.setObject(graphNode);
        }
        return graphNode;
    }

    /**
     * <PRE>
     * nodeToken -> "["
     * propertyListNotEmpty -> PropertyListNotEmpty()
     * nodeToken1 -> "]"
     * </PRE>
     */
    @Override
    public Element visit(BlankNodePropertyList n) {
        n.nodeToken.accept(this);
        Element propList = n.propertyListNotEmpty.accept(this);
        n.nodeToken1.accept(this);
        return propList;
    }

    /**
     * <PRE>
     * nodeToken -> "("
     * nodeList -> ( GraphNode() )+
     * nodeToken1 -> ")"
     * </PRE>
     */
    @Override
    public Element visit(SparqlCollection n) {
        /*
         * n.nodeToken.accept(this); n.nodeList.accept(this);
         * n.nodeToken1.accept(this);
         */
        n.nodeToken.accept(this);
        /*
         * Element collect = null; for(Node e : n.nodeList.nodes) { collect =
         * null; collect = e.accept(this); }
         */
        n.nodeToken1.accept(this);
        return null;
    }

    /**
     * <PRE>
     * nodeChoice -> VarOrTerm()
     *       | TriplesNode()
     * </PRE>
     */
    @Override
    public Element visit(GraphNode n) {
        Element varOrTermOrTriplesNode = n.nodeChoice.accept(this);
        return varOrTermOrTriplesNode;
    }

    /**
     * <PRE>
     * nodeChoice -> Var()
     *       | GraphTerm()
     * </PRE>
     */
    @Override
    public Element visit(VarOrTerm n) {
        Element varOrGraphTerm = n.nodeChoice.accept(this);
        if (varOrGraphTerm != null) {
            SPO spo = varOrGraphTerm.getSpo();
            if (spo != SPO.OBJECT && spo != SPO.PREDICATE && triplePattern.getSubject() == null) {
                varOrGraphTerm.setSpo(SPO.SUBJECT);
                triplePattern.setSubject(varOrGraphTerm);
            }
        }
        return varOrGraphTerm;
    }

    /**
     * <PRE>
     * nodeChoice -> Var()
     *       | IRIref()
     * </PRE>
     */
    @Override
    public Element visit(VarOrIRIref n) {
        Element varOrIRIRef = n.nodeChoice.accept(this);
        return varOrIRIRef;
    }

    /**
     * <PRE>
     * nodeChoice -> &lt;VAR1&gt;
     *       | &lt;VAR2&gt;
     * </PRE>
     */
    @Override
    public Element visit(Var n) {
        Element var = n.nodeChoice.accept(this);
        var.setDataType(DataType.VARIABLE);
        if (isAsteriskInSelectList || allVarsAreFromSelectList) {
            if (!selectList.contains(var.getData())) {
                selectList.add(var.getData());
            }
        }
        if(insideBuiltInCall > 0) {
            builtInCallStack.push(new ExprNode(var));
        }
        return var;
    }

    /**
     * <PRE>
     * nodeChoice -> IRIref()
     *       | RDFLiteral()
     *       | NumericLiteral()
     *       | BooleanLiteral()
     *       | BlankNode()
     *       | PutativeLiteral()
     *       | &lt;NIL&gt;
     * </PRE>
     */
    @Override
    public Element visit(GraphTerm n) {
        Element graphTerm = n.nodeChoice.accept(this);
        return graphTerm;
    }

    /**
     * ************************ Relational Expressions **************************
     * @author Slavcho Slavchev
     */

    /**
     * <PRE>
     * conditionalOrExpression -> ConditionalOrExpression()
     * </PRE>
     */
    @Override
    public ExprNode visit(Expression n) {
        ExprNode root = (ExprNode) n.conditionalOrExpression.accept(this);
        if(insideBuiltInCall > 0) {
            builtInCallStack.push(root);
        }
        return root;
    }

    /**
     * <PRE>
     * conditionalAndExpression -> ConditionalAndExpression()
     * nodeListOptional -> ( "||" ConditionalAndExpression() )*
     * </PRE>
     */
    @Override
    public ExprNode visit(ConditionalOrExpression n) {
        ExprNode root = (ExprNode) n.conditionalAndExpression.accept(this);
        return genTreeFromNodeNonChoiceList(root, n.nodeListOptional, "||");
    }

    /**
     * <PRE>
     * valueLogical -> ValueLogical()
     * nodeListOptional -> ( "&&" ValueLogical() )*
     * </PRE>
     */
    @Override
    public ExprNode visit(ConditionalAndExpression n) {
        ExprNode root = (ExprNode) n.valueLogical.accept(this);
        return genTreeFromNodeNonChoiceList(root, n.nodeListOptional, "&&");
    }

    /**
     * <PRE>
     * relationalExpression -> RelationalExpression()
     * </PRE>
     */
    @Override
    public ExprNode visit(ValueLogical n) {
        return (ExprNode) n.relationalExpression.accept(this);
    }

    /**
     * <PRE>
     * numericExpression -> NumericExpression()
     * nodeOptional -> ( "=" NumericExpression() | "!=" NumericExpression() | "&lt;" NumericExpression() | "&gt;" NumericExpression() | "&lt;=" NumericExpression() | "&gt;=" NumericExpression() )?
     * </PRE>
     */
    @Override
    public ExprNode visit(RelationalExpression n) {
        ExprNode root = (ExprNode) n.numericExpression.accept(this);
        String opsMap[] = { "=", "!=", "<", ">", "<=", ">=" };
        NodeChoice node = (NodeChoice) n.nodeOptional.node;
        if (node == null) {
            return root;
        } else {
            ExprNode parent = new ExprNode(SPO.EXPR, DataType.EXPR_OP, opsMap[node.which]);
            parent.setLeftChild(root);
            parent.setRightChild((ExprNode) node.accept(this));
            return parent;
        }
    }

    /**
     * <PRE>
     * additiveExpression -> AdditiveExpression()
     * </PRE>
     */
    @Override
    public ExprNode visit(NumericExpression n) {
        return (ExprNode) n.additiveExpression.accept(this);
    }

    /**
     * <PRE>
     * multiplicativeExpression -> MultiplicativeExpression()
     * nodeListOptional -> ( "+" MultiplicativeExpression() | "-" MultiplicativeExpression() | NumericLiteralPositive() | NumericLiteralNegative() )*
     * </PRE>
     */
    @Override
    public ExprNode visit(AdditiveExpression n) {
        ExprNode root = (ExprNode) n.multiplicativeExpression.accept(this);
        String opsMap[] = { "+", "-", "+", "+" };
        return genTreeFromNodeChoiceList(root, n.nodeListOptional, opsMap);
    }

    /**
     * <PRE>
     * unaryExpression -> UnaryExpression()
     * nodeListOptional -> ( "*" UnaryExpression() | "/" UnaryExpression() )*
     * </PRE>
     */
    @Override
    public ExprNode visit(MultiplicativeExpression n) {
        ExprNode root = (ExprNode) n.unaryExpression.accept(this);
        String opsMap[] = { "*", "/" };
        return genTreeFromNodeChoiceList(root, n.nodeListOptional, opsMap);
    }

    /**
     * <PRE>
     * nodeChoice -> ( "!" PrimaryExpression() | "+" PrimaryExpression() | "-" PrimaryExpression() | PrimaryExpression() )
     * </PRE>
     */
    @Override
    public ExprNode visit(UnaryExpression n) {
        ExprNode root = (ExprNode) n.nodeChoice.accept(this);
        String opsMap[] = { "!", "+", "-" };
        int opNum = n.nodeChoice.which;
        if (opNum == 3) {
            return root;
        } else {
            ExprNode parent = new ExprNode(SPO.EXPR, DataType.EXPR_OP, opsMap[opNum]);
            parent.setRightChild(root);
            return parent;
        }
    }

    /**
     * <PRE>
     * nodeChoice -> BrackettedExpression()
     *       | BuiltInCall()
     *       | IRIrefOrFunction()
     *       | RDFLiteral()
     *       | NumericLiteral()
     *       | BooleanLiteral()
     *       | Var()
     * </PRE>
     */
    @Override
    public ExprNode visit(PrimaryExpression n) {
        int choice = n.nodeChoice.which;
        if (choice == 0) {
            return (ExprNode) n.nodeChoice.accept(this);
        } else {
            if(choice == 1) {
                return (ExprNode) n.nodeChoice.accept(this);
            } else {
                return new ExprNode(n.nodeChoice.accept(this));
            }
        }
    }

    /**
     * <PRE>
     * nodeToken -> "("
     * expression -> Expression()
     * nodeToken1 -> ")"
     * </PRE>
     */
    @Override
    public ExprNode visit(BrackettedExpression n) {
        return (ExprNode) n.expression.accept(this);
    }

    /* Helper Method */
    public ExprNode genTreeFromNodeChoiceList(ExprNode root, NodeListOptional lst, String[] opsMap) {
        ExprNode last = root, parent_of_last = null;
        for (Node node : lst.nodes) {
            NodeChoice nodeChoice = (NodeChoice) node;
            ExprNode expr = (ExprNode) nodeChoice.accept(this);
            String exprOp = opsMap[nodeChoice.which];
            ExprNode op = new ExprNode(SPO.EXPR, DataType.EXPR_OP, exprOp);
            op.setLeftChild(last);
            op.setRightChild(expr);
            if (parent_of_last == null) {
                root = op;
            } else {
                parent_of_last.setRightChild(op);
            }
            parent_of_last = op;
            last = expr;
        }
        return root;
    }

    /* Helper Method */
    public ExprNode genTreeFromNodeNonChoiceList(ExprNode root, NodeListOptional lst, String exprOp) {
        ExprNode last = root, parent_of_last = null;
        for (Node node : lst.nodes) {
            ExprNode expr = (ExprNode) node.accept(this);
            ExprNode op = new ExprNode(SPO.EXPR, DataType.EXPR_OP, exprOp);
            op.setLeftChild(last);
            op.setRightChild(expr);
            if (parent_of_last == null) {
                root = op;
            } else {
                parent_of_last.setRightChild(op);
            }
            parent_of_last = op;
            last = expr;
        }
        return root;
    }
    
    /**
     * <PRE>
     * nodeChoice -> ( &lt;STR&gt; "(" Expression() ")"
     *       | &lt;LANG&gt; "(" Expression() ")"
     *       | &lt;LANGMATCHES&gt; "(" Expression() "," Expression() ")"
     *       | &lt;DATATYPE&gt; "(" Expression() ")"
     *       | &lt;BOUND&gt; "(" Var() ")"
     *       | &lt;SAME_TERM&gt; "(" Expression() "," Expression() ")"
     *       | &lt;IS_IRI&gt; "(" Expression() ")"
     *       | &lt;IS_URI&gt; "(" Expression() ")"
     *       | &lt;IS_BLANK&gt; "(" Expression() ")"
     *       | &lt;IS_LITERAL&gt; "(" Expression() ")"
     *       | RegexExpression() )
     * </PRE>
     */
    @Override
    public Element visit(BuiltInCall n) {
        ExprNode root = null;
        insideBuiltInCall += 1;
        int op = n.nodeChoice.which;
        n.nodeChoice.accept(this);
        switch(op) {
            case 0: root = new STR(builtInCallStack.pop()); break;
            case 1: root = new Lang(builtInCallStack.pop()); break;
            case 2:
                ExprNode rhs1 = builtInCallStack.pop();
                ExprNode lhs1 = builtInCallStack.pop();
                root = new LangMatches(lhs1, rhs1); break;
            case 3: root = new Datatype(builtInCallStack.pop()); break;
            case 4: root = new Bound(builtInCallStack.pop()); break;
            case 6: 
            case 7: root = new IsIRI(builtInCallStack.pop()); break;
            case 9: root = new IsLiteral(builtInCallStack.pop()); break;
            case 10: 
                ExprNode filter = builtInCallStack.pop();
                ExprNode rhs2 = builtInCallStack.pop();
                ExprNode lhs2 = builtInCallStack.pop();
                root = new Regex(lhs2, rhs2, (filter != null ? filter.getData() : null));
                break;
            default: System.err.print("BuiltInCall not supported!");
        }
        insideBuiltInCall -= 1;
        return root;
    }

    /* ***************************************************************** */

    /**
     * <PRE>
     * nodeToken -> &lt;REGEX&gt;
     * nodeToken1 -> "("
     * expression -> Expression()
     * nodeToken2 -> ","
     * expression1 -> Expression()
     * nodeOptional -> ( "," Expression() )?
     * nodeToken3 -> ")"
     * </PRE>
     */
    @Override
    public Element visit(RegexExpression n) {
        ExprNode lhs = (ExprNode) n.expression.accept(this);
        ExprNode rhs = (ExprNode) n.expression1.accept(this);
        ExprNode flag = (ExprNode) n.nodeOptional.accept(this);
        builtInCallStack.clear();
        builtInCallStack.push(lhs);
        builtInCallStack.push(rhs);
        builtInCallStack.push(flag);
        return null;
    }

    /**
     * <PRE>
     * iRIref -> IRIref()
     * nodeOptional -> ( ArgList() )?
     * </PRE>
     */
    @Override
    public Element visit(IRIrefOrFunction n) {
        n.nodeOptional.accept(this);
        return n.iRIref.accept(this);
    }

    /**
     *************************** Primitives ****************************
     */
    
    /**
     * <PRE>
     * string -> String()
     * nodeOptional -> ( &lt;LANGTAG&gt; | ( "^^" IRIref() ) )?
     * </PRE>
     */
    @Override
    public Element visit(RDFLiteral n) {
        Element str = n.sparqlString.accept(this);
        Element opt = n.nodeOptional.accept(this);
        if (opt != null) {
            if (doubleCarrot.equals("^^")) {
                java.lang.String string = str.getData();
                string += doubleCarrot;
                doubleCarrot = "";
                str.setData(string);
            } else {
                java.lang.String string = str.getData();
                string += atString;
                atString = "";
                str.setData(string);
            }
            java.lang.String iriRefOrTag = opt.getData();
            java.lang.String string = str.getData();
            string += iriRefOrTag;
            str.setData(string);
        }
        return str;
    }
    
    /**
     * <PRE>
     * nodeChoice -> &lt;STRING_LITERAL1&gt;
     *       | &lt;STRING_LITERAL2&gt;
     *       | &lt;STRING_LITERAL_LONG1&gt;
     *       | &lt;STRING_LITERAL_LONG2&gt;
     * </PRE>
     */
    @Override
    public Element visit(SparqlString n) {
        Element stringLit = n.nodeChoice.accept(this);
        stringLit.setDataType(DataType.LITERAL);
        return stringLit;
    }
    
    /**
     * <PRE>
     * nodeChoice -> &lt;IRI_REF&gt;
     *       | PrefixedName()
     * </PRE>
     */
    @Override
    public Element visit(IRIref n) {
        Element iriRefOrPrefixedName = n.nodeChoice.accept(this);
        iriRefOrPrefixedName.setDataType(DataType.URL);
        filterPrefixesInElement(iriRefOrPrefixedName);
        return iriRefOrPrefixedName;
    }

    /**
     * <PRE>
     * nodeChoice -> &lt;PNAME_LN&gt;
     *       | &lt;PNAME_NS&gt;
     * </PRE>
     */
    @Override
    public Element visit(PrefixedName n) {
        Element prefixedName = n.nodeChoice.accept(this);
        return prefixedName;
    }
    
    /**
     * <PRE>
     * nodeChoice -> ( NumericLiteralUnsigned() | NumericLiteralPositive() | NumericLiteralNegative() )
     * </PRE>
     */
    @Override
    public Element visit(NumericLiteral n) {
        Element numLit = n.nodeChoice.accept(this);
        numLit.setDataType(DataType.NUMBER);
        return numLit;
    }

    /**
     * <PRE>
     * nodeChoice -> &lt;INTEGER&gt;
     *       | &lt;DECIMAL&gt;
     *       | &lt;DOUBLE&gt;
     * </PRE>
     */
    @Override
    public Element visit(NumericLiteralUnsigned n) {
        Element numLitUn = n.nodeChoice.accept(this);
        return numLitUn;
    }

    /**
     * <PRE>
     * nodeChoice -> &lt;INTEGER_POSITIVE&gt;
     *       | &lt;DECIMAL_POSITIVE&gt;
     *       | &lt;DOUBLE_POSITIVE&gt;
     * </PRE>
     */
    @Override
    public Element visit(NumericLiteralPositive n) {
        Element numLitPos = n.nodeChoice.accept(this);
        return numLitPos;
    }

    /**
     * <PRE>
     * nodeChoice -> &lt;INTEGER_NEGATIVE&gt;
     *       | &lt;DECIMAL_NEGATIVE&gt;
     *       | &lt;DOUBLE_NEGATIVE&gt;
     * </PRE>
     */
    @Override
    public Element visit(NumericLiteralNegative n) {
        Element numNegLit = n.nodeChoice.accept(this);
        return numNegLit;
    }

    /**
     * <PRE>
     * nodeChoice -> &lt;TRUE&gt;
     *       | &lt;FALSE&gt;
     * </PRE>
     */
    @Override
    public Element visit(BooleanLiteral n) {
        Element boolLit = n.nodeChoice.accept(this);
        boolLit.setDataType(DataType.NUMBER);
        return boolLit;
    }

    /**
     * <PRE>
     * nodeChoice -> &lt;BLANK_NODE_LABEL&gt;
     *       | &lt;ANON&gt;
     * </PRE>
     */
    @Override
    public Element visit(BlankNode n) {
        Element blnkNode = n.nodeChoice.accept(this);
        blnkNode.setDataType(DataType.BLANK_NODE);
        return blnkNode;
    }

    /**
     * <PRE>
     * nodeToken -> &lt;PUTATIVE_LITERAL&gt;
     * </PRE>
     */
    @Override
    public Element visit(PutativeLiteral n) {
        Element putLit = n.nodeToken.accept(this);
        putLit.setDataType(DataType.LITERAL);
        return putLit;
    }

    /**
     ******************************************************************
     */
    
    /**
     * <PRE>
     * nodeChoice -> Collection()
     * | BlankNodePropertyList()
     * </PRE>
     */
    @Override
    public Element visit(TriplesNode n) {
        Element collectOrBlank = n.nodeChoice.accept(this);
        return collectOrBlank;
    }
}