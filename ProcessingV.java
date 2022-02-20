package implementation;

/**
 * Represents the processing we apply to a vertex of a graph.
 */
public interface ProcessingV<E> {
	public void processVertex(String vertex, E vertexData);
}

