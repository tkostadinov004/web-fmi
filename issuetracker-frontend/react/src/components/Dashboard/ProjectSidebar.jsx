import { useState } from "react";

const PROJECT_COUNT_VISIBLE = 4;

const ProjectMenu = ({ projects, query, onQueryChange, onProjectSelect }) => {
  const filtered = projects.filter((p) =>
    p.name.toLowerCase().includes(query.toLowerCase())
  );
 
  return (
    <div className="popup-menu">
      <label>Projects</label>
      <input
        type="search"
        placeholder="Search..."
        name="projectName"
        value={query}
        onChange={(e) => onQueryChange(e.target.value)}
      />
      <div className="project-search-result">
        {query.trim() !== '' &&
          filtered.map((project) => (
            <button
              key={project.id}
              className="project-button"
              onClick={() => onProjectSelect(project.id)}
            >
              {project.name}
            </button>
          ))}
      </div>
    </div>
  );
};
 
export const ProjectSidebar = ({
  projects,
  selectedProjectId,
  onProjectSelect,
  onCreateProject,
}) => {
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const [query, setQuery] = useState('');
 
  const visibleProjects = projects.slice(0, PROJECT_COUNT_VISIBLE);
 
  return (
    <aside className="sidebar">
      <div className="sidebar-header">
        <h1>Projects</h1>
      </div>
 
      <nav className="projects-list">
        {visibleProjects.map((project) => (
          <button
            key={project.id}
            className={`project-button ${project.id === selectedProjectId ? 'active-project' : ''}`}
            onClick={() => onProjectSelect(project.id)}
          >
            {project.name}
          </button>
        ))}
 
        <div className="project-menu">
          <button
            className="project-button"
            onClick={() => setIsMenuOpen((prev) => !prev)}
          >
            More projects →
          </button>
 
          {isMenuOpen && (
            <ProjectMenu
              projects={projects}
              query={query}
              onQueryChange={setQuery}
              onProjectSelect={(id) => {
                onProjectSelect(id);
                setIsMenuOpen(false);
                setQuery('');
              }}
            />
          )}
        </div>
 
        <button className="signup-btn" onClick={onCreateProject}>
          Create project
        </button>
      </nav>
    </aside>
  );
};