package implementation;

import java.util.*;

/**
 * Implements a graph. We use two maps: one map for adjacency properties 
 * (adjancencyMap) and one map (dataMap) to keep track of the data associated 
 * with a vertex. 
 * 
 * CMSC132 project
 * 
 * @param <E>
 */
public class Graph<E> {
	/* You must use the following maps in your implementation */
	private HashMap<String, HashMap<String, Integer>> adjacencyMap;
	private HashMap<String, E> dataMap;

	public Graph() {
		this.adjacencyMap = new HashMap<>();
		this.dataMap = new HashMap<>();
	}
	
	public void addDirectedEdge(String startVertexName, String endVertexName, int cost) {
		if (!this.adjacencyMap.containsKey(startVertexName) || !this.adjacencyMap.containsKey(endVertexName)) {
			throw new IllegalArgumentException("Vertices are not part of the graph");
		}
		this.adjacencyMap.get(startVertexName).put(endVertexName, cost);
	}
	
	public void addVertex(String vertexName, E data) {
		if (this.adjacencyMap.containsKey(vertexName)) {
			throw new IllegalArgumentException("Vertices are not part of the graph");
		}
		this.adjacencyMap.put(vertexName, new HashMap<>());
		this.dataMap.put(vertexName, data);
	}
	
	public Map<String, Integer> getAdjacentVertices(String vertexName) {
		return this.adjacencyMap.get(vertexName);
	}
	
	public int getCost(String startVertexName, String endVertexName) {
		return this.adjacencyMap.get(startVertexName).get(endVertexName);
	}
	
	public E getData(String vertexName) {
		if (!this.dataMap.containsKey(vertexName)) {
			throw new IllegalArgumentException("Vertices are not part of the graph");
		}
		return this.dataMap.get(vertexName);
	}
	
	public Set<String> getVertices() {
		return this.adjacencyMap.keySet();
	}
	
	public void doBreadthFirstSearch(String startVertexName, CallBack<E> callback) {
		if (!this.adjacencyMap.containsKey(startVertexName)) {
			throw new IllegalArgumentException("Vertices are not part of the graph");
		}
		Set<String> visitedNodes = new HashSet<>();
		Queue<String> workList = new LinkedList<>();
		workList.add(startVertexName);
		do {
			String curNode = workList.peek();
			workList.remove();
			if (visitedNodes.contains(curNode)) {
				continue;
			}
			visitedNodes.add(curNode);
			ArrayList<String> neighbors = new ArrayList<>((new TreeMap<>(this.adjacencyMap.get(curNode))).descendingKeySet());
			
			for (int i = neighbors.size()-1; i >= 0; i--) {
				String n = neighbors.get(i);
				workList.add(n);
			}
			callback.processVertex(curNode, this.dataMap.get(curNode));
		} while (workList.size() > 0);
	}
	
	public void doDepthFirstSearch(String startVertexName, CallBack<E> callback) {
		if (!this.adjacencyMap.containsKey(startVertexName)) {
			throw new IllegalArgumentException("Vertices are not part of the graph");
		}
		Set<String> visitedNodes = new HashSet<>();
		Stack<String> workStack = new Stack<>();
		workStack.push(startVertexName);
		do {
			String curNode = workStack.peek();
			workStack.pop();
			if (visitedNodes.contains(curNode)) {
				continue;
			}
			visitedNodes.add(curNode);
			ArrayList<String> neighbors = new ArrayList<>((new TreeMap<>(this.adjacencyMap.get(curNode))).descendingKeySet());
			
			for (int i = neighbors.size()-1; i >= 0; i--) {
				String n = neighbors.get(i);
				workStack.push(n);
			}
			callback.processVertex(curNode, this.dataMap.get(curNode));
		} while (workStack.size() > 0);
	}
	
	public int doDijkstras(String startVertexName, String endVertexName, ArrayList<String> shortestPath) {
		if (!this.adjacencyMap.containsKey(startVertexName) || !this.adjacencyMap.containsKey(endVertexName)) {
			throw new IllegalArgumentException("Vertices are not part of the graph");
		}
		Set<String> visitedNodes = new HashSet<>();
		Queue<String> workList = new LinkedList<>();
		
		shortestPath.clear();
		
		Map<String, Pair<String, Integer>> toFromCost = new HashMap<>();
		for (String key : this.adjacencyMap.keySet()) {
			toFromCost.put(key, new Pair<String, Integer>("-", Integer.MAX_VALUE));
		}
		
		toFromCost.put(startVertexName, new Pair<String, Integer>("-", 0));
		workList.add(startVertexName);
		String curNode;
		do {
			curNode = workList.peek();
			workList.remove();
			if (visitedNodes.contains(curNode)) {
				continue;
			}
			ArrayList<String> neighbors = new ArrayList<>(new TreeMap<String, Integer>(this.adjacencyMap.get(curNode)).keySet());
			for (int i = neighbors.size()-1; i >= 0; i--) {
				String neighbor = neighbors.get(i);
				int newCost = toFromCost.get(curNode).getSecond() + this.adjacencyMap.get(curNode).get(neighbor);
				if (toFromCost.get(neighbor).getSecond() > newCost) {
					toFromCost.put(neighbor, new Pair<String, Integer>(curNode, newCost));
				}
				workList.add(neighbor);
			}
			visitedNodes.add(curNode);
		} while (workList.size() > 0);
		
		curNode = endVertexName;
		
		while (!toFromCost.get(curNode).getFirst().equals("-")) {
			shortestPath.add(0, curNode);
			curNode = toFromCost.get(curNode).getFirst();
		}
		shortestPath.add(0, curNode);
		
		if (toFromCost.get(endVertexName).getSecond() == Integer.MAX_VALUE) {
			shortestPath.clear();
			shortestPath.add("None");
			return -1;
		}
		return toFromCost.get(endVertexName).getSecond();
	}
	
	@Override
	public String toString() {
		String output = "";
		output += "Vertices: [";
		Set<String> vertices = this.adjacencyMap.keySet();
		TreeMap<String, TreeMap<String, Integer>> treeAdjMap = new TreeMap<>();
		for (String vertex : vertices) {
			treeAdjMap.put(vertex, new TreeMap<>(this.adjacencyMap.get(vertex)));
		}
		ArrayList<String> inorderVertices = new ArrayList<>(treeAdjMap.descendingKeySet());
		for (int i = inorderVertices.size()-1; i >= 0; i--) {
			output += inorderVertices.get(i);
			if (i > 0) {
				output += ", ";
			}
		}
		output += "]\nEdges:";
		for (int i = inorderVertices.size()-1; i >= 0; i--) {
			String fromVertex = inorderVertices.get(i);
			output += "\nVertex(" + fromVertex + ")--->{";
			ArrayList<String> toVertices = new ArrayList<>(treeAdjMap.get(fromVertex).descendingKeySet());
			for (int j = toVertices.size()-1; j >= 0; j--) {
				String toVertex = toVertices.get(j);
				output += toVertex + "=" + getCost(fromVertex, toVertex);
				if (j > 0) {
					output += ", ";
				}
			}
			output += "}";
		}
		return output;
	}
	
	private class Pair<T1, T2> {
		private T1 first;
		private T2 second;
		public Pair(T1 first, T2 second) {
			this.first  = first;
			this.second = second;
		}
		public T1 getFirst() {
			return first;
		}
		public T2 getSecond() {
			return second;
		}
		
	}
}
