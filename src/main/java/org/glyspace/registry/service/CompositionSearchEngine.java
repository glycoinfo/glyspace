package org.glyspace.registry.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eurocarbdb.MolecularFramework.sugar.GlycoEdge;
import org.eurocarbdb.MolecularFramework.sugar.GlycoNode;
import org.eurocarbdb.MolecularFramework.sugar.GlycoconjugateException;
import org.eurocarbdb.MolecularFramework.sugar.Sugar;
import org.eurocarbdb.MolecularFramework.util.similiarity.SearchEngine.EdgeComparator;
import org.eurocarbdb.MolecularFramework.util.similiarity.SearchEngine.MatrixDataObject;
import org.eurocarbdb.MolecularFramework.util.similiarity.SearchEngine.NodeComparator;
import org.eurocarbdb.MolecularFramework.util.similiarity.SearchEngine.NodeComparatorWithSubstituents;
import org.eurocarbdb.MolecularFramework.util.similiarity.SearchEngine.SearchEngineException;
import org.eurocarbdb.MolecularFramework.util.similiarity.SearchEngine.SearchVisitor;
import org.eurocarbdb.MolecularFramework.util.similiarity.SearchEngine.StandardEdgeComparator;
import org.eurocarbdb.MolecularFramework.util.similiarity.SearchEngine.StandardNodeComparator;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorCountNodeType;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;

public class CompositionSearchEngine {

	private Vector <MatrixDataObject> v_query = new Vector <MatrixDataObject> ();
	private Vector <MatrixDataObject> v_queried = new Vector <MatrixDataObject> ();
	
	private NodeComparator NodeComparator = new NodeComparatorWithSubstituents();
	private EdgeComparator EdgeComparator= new StandardEdgeComparator();
	private Sugar queried = null;
	private Sugar query = null;
	private int[][] m_aMatrix;
	private int score=0;
	
	private MatrixDataObject queriedMax = new MatrixDataObject ();
	private MatrixDataObject queryMax = new MatrixDataObject ();
	
	/**
	 * Set structure to be queried. 
	 * @throws GlycoVisitorException 
	 */
	public void setQueriedStructure ( Sugar queriedStructure) throws GlycoVisitorException{
		this.queried=queriedStructure;
		// traverse graphs and vector with MDO "flat tree" data structure		
		SearchVisitor sv = new SearchVisitor ();
		// Vector queried
		sv.start(queried);
		this.v_queried=sv.getVector();
	}
	/**
	 * Set query structure. 
	 */
	public void setQueryStructure ( Sugar queryStructure) throws GlycoVisitorException{
		GlycoVisitorCountNodeType gvnt = new GlycoVisitorCountNodeType ();		
		gvnt.start(queryStructure);
		if (gvnt.getRepeatCount()>0){
			throw new GlycoVisitorException ("No Repeats as queries. Expand Repeat query!");
		}		
		this.query=queryStructure;
		SearchVisitor sv1 = new SearchVisitor ();
		// Vector query
		sv1.start(query);
		this.v_query=sv1.getVector();
	}
	/**
	 * Set comparator for the residues. 
	 */
	public void setNodeComparator ( NodeComparator NodeComparator){
		this.NodeComparator = NodeComparator;
	}
	/**
	 * Set comparator for the linkages. 
	 */
	public void setEdgeComparator ( EdgeComparator EdgeComparator){
		this.EdgeComparator = EdgeComparator;
	}	
	
	/**
	 * Checks, if query and queried structure are exactly equivalent.
	 * @return Boolean, true if exact match, false if not exact match.
	 */
	public Boolean isExactMatch () throws SearchEngineException{
		try {
			this.match();
		} catch (GlycoVisitorException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (GlycoconjugateException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (SearchEngineException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		GlycoVisitorCountNodeType g_count = new GlycoVisitorCountNodeType();
		try {
			g_count.start(this.query);
		} catch (GlycoVisitorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Integer count = g_count.getMonosaccharideCount()+
		g_count.getNonMonosaccharideCount()+
		g_count.getSubstituentCount()+
		g_count.getUnvalidatedNodeCount();

		if (this.getMax().equals(count)){
			return (true);
		}
		else {
			return (false);
		}
	}
	
	/**
	 * remove already matched entries from v_queried vector
	 */
	public void removeMatchedFromQueried () {
		// remove queriedMax from the v_queried vector	
		// and all its matched children
		List<MatrixDataObject> itemsToRemove = new ArrayList<MatrixDataObject>();
		removeMatchedChildren (this.queriedMax, this.queryMax, itemsToRemove);
		v_queried.remove(this.queriedMax);
		for (Iterator iterator = itemsToRemove.iterator(); iterator.hasNext();) {
			MatrixDataObject matrixDataObject = (MatrixDataObject) iterator
					.next();
			v_queried.remove(matrixDataObject);
		}
	}
	
	public void removeMatchedChildren (MatrixDataObject queriedM, MatrixDataObject queryM, List<MatrixDataObject> itemsToRemove) {
		// iterate over all children
		for (GlycoEdge t_childEdge_queried : queriedM.getChildren()) {
			for (GlycoEdge t_childEdge_query : queryM.getChildren()){

				// for identical children, descend
				if (this.EdgeComparator.compare(t_childEdge_queried, t_childEdge_query) == 0 &&
						this.NodeComparator.compare(t_childEdge_queried.getChild(),t_childEdge_query.getChild()) == 0){

					// iterate over vector 
					for (MatrixDataObject a : v_queried){
						for (MatrixDataObject b : v_query){
							if (t_childEdge_queried.getChild()==a.getNode() 
									&& 	t_childEdge_query.getChild()==b.getNode() ) {
								itemsToRemove.add(a);
								removeMatchedChildren(a, b, itemsToRemove);
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * 
	 * @return the number of entries left in v_queried vector
	 */
	public int getNotMatchedCount () {
		return v_queried.size();
	}
	
	public void match () throws GlycoVisitorException, GlycoconjugateException, SearchEngineException{
		if (this.queried==null || this.query==null){			
			throw new SearchEngineException ("You forgot to add sugars");
		}

		// Perform Matrix initialization
		this.m_aMatrix = new int[v_queried.size()][v_query.size()];

		// iterate over all matrix entries and start recursion		
		for (int i = 0; i < v_queried.size(); i++) {
			for (int q = 0; q < v_query.size(); q++) {				
				this.score=0;
				for (MatrixDataObject mdo : this.v_queried){
					mdo.setVisited(false);
				}
				for (MatrixDataObject mdo : this.v_query){
					mdo.setVisited(false);
				}			
				this.m_aMatrix[i][q] = recursive (v_queried.get(i),v_query.get(q));
			}
		}
	}
	
	private int recursive(MatrixDataObject MDO_queried, MatrixDataObject MDO_query) throws GlycoconjugateException {

		GlycoNode queriedNode = MDO_queried.getNode();
		GlycoNode queryNode = MDO_query.getNode();

		if (this.NodeComparator.compare(queriedNode, queryNode)==0){
			// identity
			this.score++;		

			// iterate over all children
			for (GlycoEdge t_childEdge_queried : MDO_queried.getChildren()){
				for (GlycoEdge t_childEdge_query : MDO_query.getChildren()){

					// for identical children, descend
					if (this.EdgeComparator.compare(t_childEdge_queried, t_childEdge_query) == 0 &&
							this.NodeComparator.compare(t_childEdge_queried.getChild(),t_childEdge_query.getChild()) == 0){

						// iterate over vector 
						for (MatrixDataObject a : v_queried){
							for (MatrixDataObject b : v_query){

								// recursion
								if (t_childEdge_queried.getChild()==a.getNode() 
										&& 	t_childEdge_query.getChild()==b.getNode()  
										&& b.getVisited()==false 
										&& this.score<v_query.size()
								){
									// block current node								
									block(b);									
									recursive (a,b);
								}								
							}
						}
					}
				}
			}
		}		
		return score;

	}

	private void block(MatrixDataObject mdo_queried) {
		for (MatrixDataObject mdo : this.v_queried){
			if (mdo_queried==mdo){
				mdo.setVisited(true);
			}
		}
		for (MatrixDataObject mdo : this.v_query){
			if (mdo_queried==mdo){
				mdo.setVisited(true);
			}
		}
	}
	
	private Integer getMax() {
		this.score=0;

		//	get max score from matrix regardless of reducing end
		Integer temp=0;			
		for (int t_counterG1 = 0; t_counterG1 < this.v_queried.size(); t_counterG1++) {			
			for (int t_counterG2 = 0; t_counterG2 < this.v_query.size(); t_counterG2++){							
				if (this.m_aMatrix[t_counterG1][t_counterG2]>temp){	
					temp=this.m_aMatrix[t_counterG1][t_counterG2];
					this.score=temp;
					this.queriedMax=v_queried.get(t_counterG1);
					this.queryMax=v_query.get(t_counterG2);
				}					
			}				
		}

		return this.score;
	}
}
