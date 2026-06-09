package bg.sofia.uni.fmi.issuetracker.repository.neo;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.SessionConfig;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class NeoProjectRepositoryImpl implements NeoProjectRepository {
    private final Driver driver;
    private final SessionConfig sessionConfig;

    public NeoProjectRepositoryImpl(Driver driver, SessionConfig sessionConfig) {
        this.driver = driver;
        this.sessionConfig = sessionConfig;
    }

    @Override
    public void addProject(String projectId) {
        try (Session session = driver.session(sessionConfig)) {
            session.executeWrite(tx -> {
                tx.run("""
                        MERGE (p: Project {id: $projectId})
                        """, Map.of("projectId", projectId)).consume();
                return null;
            });
        }
    }

    @Override
    public void deleteProject(String projectId) {
        try (Session session = driver.session(sessionConfig)) {
            session.executeWrite(tx -> {
                tx.run("""
                        MATCH r = (p: Project {id: $projectId})-[*]->(s) DETACH DELETE r;
                        """, Map.of("projectId", projectId)).consume();
                return null;
            });
        }
    }
}
