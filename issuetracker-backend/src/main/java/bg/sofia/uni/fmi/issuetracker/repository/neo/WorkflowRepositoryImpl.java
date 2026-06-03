package bg.sofia.uni.fmi.issuetracker.repository.neo;

import bg.sofia.uni.fmi.issuetracker.dto.input.project.workflow.ProjectWorkflowDTO;
import bg.sofia.uni.fmi.issuetracker.dto.input.project.workflow.WorkflowTransitionDTO;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.SessionConfig;
import org.neo4j.driver.Value;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class WorkflowRepositoryImpl implements WorkflowRepository {
    private final Driver driver;
    private final SessionConfig sessionConfig;

    public WorkflowRepositoryImpl(Driver driver, SessionConfig sessionConfig) {
        this.driver = driver;
        this.sessionConfig = sessionConfig;
    }

    @Override
    public ProjectWorkflowDTO getWorkflow(String projectId) {
        try (Session session = driver.session(sessionConfig)) {
            org.neo4j.driver.Record query = session.executeRead(tx ->
                    tx.run("""
                            MATCH (p:Project {id: $projectId})-[:WORKFLOW]->(initial:Status)
                            MATCH (initial)-[:TRANSITION*0..]->(s:Status)
                            WITH initial, collect(DISTINCT s) AS allStatuses
                            
                            UNWIND allStatuses AS statusNode
                            MATCH (statusNode)-[r:TRANSITION]->(:Status)
                            WITH initial, allStatuses, collect(DISTINCT r) AS relationships
                            
                            RETURN
                              [status IN allStatuses | status.name] AS workflowStatuses,
                              initial.name AS initialStatus,
                              [rel IN relationships | {
                                source: startNode(rel).name,
                                target: endNode(rel).name
                              }] AS transitions
                            """, Map.of("projectId", projectId)).single());
            return new ProjectWorkflowDTO(
                    query.get("workflowStatuses").asList(Value::asString),
                    query.get("initialStatus").asString(),
                    query.get("transitions").asList(v -> v.as(WorkflowTransitionDTO.class))
            );
        }
    }

    @Override
    public void createWorkflow(String projectId, ProjectWorkflowDTO dto) {
        try (Session session = driver.session(sessionConfig)) {
            session.executeWrite(tx -> {
                tx.run("""
                        MATCH (p: Project {id: $projectId})
                        MERGE (p)-[w: WORKFLOW]->(s: Status {name: $initialStatusName});
                        """, Map.of("projectId", projectId, "initialStatusName", dto.initialStatus())).consume();
                return null;
            });
            for (WorkflowTransitionDTO transition : dto.transitions()) {
                session.executeWrite(tx -> {
                    tx.run("""
                            MATCH (p: Project {id: $projectId})-[*]->(s: Status {name: $source})
                            MERGE (s)-[r: TRANSITION]->(n: Status {name: $target});
                            """, Map.of("projectId", projectId, "source", transition.source(), "target", transition.target())).consume();
                    return null;
                });
            }
        }
    }

    @Override
    public void deleteWorkflow(String projectId) {
        try (Session session = driver.session(sessionConfig)) {
            session.executeWrite(tx -> {
                tx.run("""
                        MATCH (p: Project {id: $projectId})-[*]->(e: Status) DETACH DELETE e;
                        """, Map.of("projectId", projectId)).consume();
                return null;
            });
        }
    }

    @Override
    public boolean isTransitionPossible(String projectId, String source, String target) {
        if (source.equals(target)) {
            return true;
        }

        try (Session session = driver.session(sessionConfig)) {
            List<org.neo4j.driver.Record> query = session.executeRead(tx ->
                    tx.run("""
                            RETURN EXISTS {
                                MATCH (p: Project {id: $projectId})-[*]->(source: Status {name: $source})-[rel: TRANSITION]->(target: Status {name: $target})
                            } as exists;
                            """, Map.of("projectId", projectId, "source", source, "target", target)).list());
            return !query.isEmpty() && query.getFirst().get("exists").asBoolean();
        }
    }

    @Override
    public List<String> getStatuses(String projectId) {
        try (Session session = driver.session(sessionConfig)) {
            List<org.neo4j.driver.Record> query = session.executeRead(tx ->
                    tx.run("""
                            MATCH (p: Project {id: $projectId})-[*]->(s: Status) RETURN s.name as status_name;
                            """, Map.of("projectId", projectId)).list());
            return query.stream().map(r -> r.get("status_name").asString()).toList();
        }
    }
}
