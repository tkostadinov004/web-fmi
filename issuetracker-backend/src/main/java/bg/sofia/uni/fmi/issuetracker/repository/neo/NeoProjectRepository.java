package bg.sofia.uni.fmi.issuetracker.repository.neo;

/**
 * Repository interface for Neo4j project graph operations.
 * <p>
 * This repository is responsible for creating and removing project nodes
 * in the Neo4j graph database.
 */
public interface NeoProjectRepository {
    /**
     * Creates a Neo4j node for the project with the given identifier.
     *
     * @param projectId the unique identifier of the project
     */
    void addProject(String projectId);

    /**
     * Deletes the Neo4j node for the project with the given identifier.
     *
     * @param projectId the unique identifier of the project
     */
    void deleteProject(String projectId);
}
