import { useState } from "react";
import { NavLink, Link, useNavigate } from "react-router-dom";
import CreateProjectPage from "../CreateProjectPage/createProject";

const PROJECT_COUNT_VISIBLE = 4;

const ProjectMenu = ({ projects, query, onQueryChange }) => {
  const filtered = projects.filter((p) =>
    p.name.toLowerCase().includes(query.toLowerCase()),
  );
  const navigate = useNavigate();
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
        {query.trim() !== "" &&
          filtered.map((project) => (
            <Link
              to={`/dashboard/${project.uuid}`}
              className={`project-button text-decoration-none text-dark`}
            >
              {project.name}
            </Link>
          ))}
      </div>
    </div>
  );
};

export const ProjectSidebar = ({
  projects,
  selectedProjectId,
  onCreateProject,
}) => {
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const [query, setQuery] = useState("");

  const visibleProjects = projects.slice(0, PROJECT_COUNT_VISIBLE);

  return (
    <aside className="sidebar">
      <div className="sidebar-header">
        <NavLink to={`/home`} className={`text-decoration-none text-dark`}>
          <h1>Projects</h1>
        </NavLink>
      </div>

      <nav className="projects-list">
        {visibleProjects.map((project) => (
          <NavLink
            to={`/dashboard/${project.uuid}`}
            className={`project-button ${project.uuid === selectedProjectId ? "active-project" : ""} text-decoration-none text-dark`}
          >
            {project.name}
          </NavLink>
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
            />
          )}
        </div>

        <CreateProjectPage onProjectCreated={(projectId) => {navigate(`dashboard/${projectId}`)}}/>
      </nav>
    </aside>
  );
};
