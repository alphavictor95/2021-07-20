package it.polito.tdp.yelp.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.yelp.db.YelpDao;

public class Model {
	private YelpDao dao;
	private SimpleWeightedGraph<User, DefaultWeightedEdge> grafo;
	private Map<String, User> idMap;
	
	public Model() {
	dao = new YelpDao();
	idMap = new HashMap<>();
	}
	
	
	
	
	
	public String creaGrafo(int numeroRecensioni, int anno) {
		grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		// come popolo la idMap?
		//passandola al dao e facendomela riempire
		dao.getUsers(idMap, numeroRecensioni);
		Graphs.addAllVertices(this.grafo, idMap.values());
		for(User u1 : idMap.values()) {
			for(User u2 : idMap.values()) {
				if(!u1.equals(u2) && u1.getUserId().compareTo(u2.getUserId())<0) {
					// importante la seconda condizione per non contare due volte
					int sim = dao.calcolaSimilarita(u1, u2, anno);
					if(sim>0) {
						Graphs.addEdge(this.grafo, u1, u2, sim);
					}
				}
			}
		}
		return "Grafo creato con "+this.grafo.vertexSet().size()+
				" vertici e "+ this.grafo.edgeSet().size() + " archi\n";
	}
	public List<User> getUsers() {
		List<User> users= new ArrayList<User>(idMap.values());
		return users;
	}





	public List<User> getSimili(User simile) {
		int max = 0;
		for(DefaultWeightedEdge e : this.grafo.edgesOf(simile)) {
			if(this.grafo.getEdgeWeight(e)>max) {
				max = (int) this.grafo.getEdgeWeight(e);
			}	
		}
		List<User> result = new ArrayList<>();
		for(DefaultWeightedEdge e : this.grafo.edgesOf(simile)) {
			if((int)this.grafo.getEdgeWeight(e)==max) {
				User u2 = Graphs.getOppositeVertex(grafo, e, simile);
				result.add(u2);
			}
		}
		return result;
	}
}
