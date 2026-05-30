package bg.sofia.uni.fmi.issuetracker.repository.neo;

public interface NeoProjectRepository {
    void addProject(String projectId);

    void deleteProject(String projectId);
}
