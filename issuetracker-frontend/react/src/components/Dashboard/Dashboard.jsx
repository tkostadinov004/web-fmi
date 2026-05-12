import React, { useState } from "react";
import { NavLink, useNavigate } from "react-router-dom";
import "./Dashboard.css";

const PROJECT_COUNT_VISUALIZED = 4;
const Dashboard = () => {
  const projects = [
    "Project Issue Tracker",
    "Marketing Platform",
    "Company CRM",
    "Internal Dashboard",
    "Internal Dashboard",
  ];

  const tickets = [
    {
      id: "KAN-1",
      title: "Create login page",
      assignee: "John Doe",
      priority: "Medium",
      status: "TODO",
    },
    {
      id: "KAN-2",
      title: "Implement auth service",
      assignee: "Sarah Smith",
      priority: "High",
      status: "IN PROGRESS",
    },
    {
      id: "KAN-3",
      title: "Setup API integration",
      assignee: "Alex Johnson",
      priority: "Low",
      status: "DONE",
    },
  ];

  const users = ["John Doe", "Sarah Smith", "Alex Johnson", "Emily Brown"];

  const [isProjectMenuOpen, openProjectMenu] = useState(false);
  const [isFilterMenuOpen, openFilterMenu] = useState(false);
  const [projectQuery, setProjectQuery] = useState("");
  const [activeFilter, setActiveFilter] = useState("assignee");
  const [selectedFilters, setSelectedFilters] = useState([]);

  const filteredProjects = projects.filter((project) =>
    project.toLowerCase().includes(projectQuery.toLowerCase()),
  );
  const assignees = [...new Set(tickets.map(t => t.assignee))];
  const statuses = [...new Set(tickets.map(t => t.status))];
  const currentOptions =
    activeFilter === "assignee"
      ? assignees
      : statuses;
  const filteredTickets = tickets.filter(ticket => {
    if (selectedFilters.length === 0) return true;

    if (activeFilter === "assignee") {
      return selectedFilters.includes(ticket.assignee);
    }

    if (activeFilter === "status") {
      return selectedFilters.includes(ticket.status);
    }

    return true;
  });

  return (
    <div className="dashboard-layout">
      <aside className="sidebar">
        <div className="sidebar-header">
          <h1>Projects</h1>
        </div>

        <nav className="projects-list">
          {loadProjectBtn(projects)}
          <div className="project-menu">
            <button
              className="project-button"
              onClick={() => openProjectMenu(!isProjectMenuOpen)}
            >
              More projects →
            </button>
            {isProjectMenuOpen && (
              <div className="popup-menu">
                <label>Projects</label>
                <input
                  type="search"
                  placeholder="Search..."
                  name="projectName"
                  onChange={(text) => setProjectQuery(text.target.value)}
                />
                <div className="project-search-result">
                  {projectQuery.trim() !== "" &&
                    filteredProjects.map((project, index) => (
                      <button key={project} className="project-button ">
                        {project}
                      </button>
                    ))}
                </div>
              </div>
            )}
          </div>
          <button className="signup-btn">Create project</button>
        </nav>
      </aside>

      <main className="main-content">
        <div className="top-header">
          <div>
            <h2 className="project-title">Project Issue Tracker</h2>
            <p className="project-subtitle">Current tickets and team members</p>
          </div>

          <div className="users-list">
            {users.map((user) => (
              <div className="user-avatar" key={user} title={user}>
                {user
                  .split(" ")
                  .map((name) => name[0])
                  .join("")}
              </div>
            ))}
          </div>
        </div>
        <div className="table-menus">
          <input
            type="search"
            placeholder="Search..."
            name="taskSearch"
          ></input>
          <div className="filter-menu">
            <button
              className="filter-button"
              onClick={() => openFilterMenu(!isFilterMenuOpen)}
            >
              {" "}
              Filter{" "}
            </button>
            {isFilterMenuOpen && (
              <div className="filter-popup-menu">
                <div className="filter-left">
                  <button
                    className="project-button"
                    onClick={() => setActiveFilter("assignee")}
                  >
                    Assignee
                  </button>

                  <button
                    className="project-button"
                    onClick={() => setActiveFilter("status")}
                  >
                    Status
                  </button>
                </div>

                <div className="filter-right">
                  <form>
                    {currentOptions.map(option => (
                      <div className="filter-result-row" key={option}>
                        <input
                          type="checkbox"
                          value={option}
                          checked={selectedFilters.includes(option)}

                          onChange={() => handleCheckboxChange(option)}

                        />

                        <label>{option}</label>
                      </div>
                    ))}
                  </form>
                </div>
              </div>
            )}
          </div>
        </div>
        <div className="table-container">
          <table>
            <thead>
              <tr>
                <th>Ticket</th>
                <th>Assignee</th>
                <th>Priority</th>
                <th>Status</th>
              </tr>
            </thead>

            <tbody>
              {filteredTickets.map((ticket) => (
                <tr key={ticket.id}>
                  <td>
                    <NavLink to={`/ticket/${ticket.id}`}>
                      <div className="table-ticket-id">{ticket.id}</div>
                      <div className="table-ticket-title">{ticket.title}</div>
                    </NavLink>
                  </td>

                  <td>{ticket.assignee}</td>

                  <td>
                    <span className="priority-badge">{ticket.priority}</span>
                  </td>

                  <td>
                    <span
                      className={`status-badge ${ticket.status === "DONE"
                        ? "status-done"
                        : ticket.status === "IN PROGRESS"
                          ? "status-progress"
                          : "status-todo"
                        }`}
                    >
                      {ticket.status}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </main>
    </div>
  );

  function handleCheckboxChange(value) {
  setSelectedFilters(prev =>
    prev.includes(value)
      ? prev.filter(v => v !== value)
      : [...prev, value]
  );
  }
};

function loadProjectBtn(projects) {
  return projects.slice(0, PROJECT_COUNT_VISUALIZED).map((project, index) => (
    <button
      key={project}
      className={`project-button ${index === 0 ? "active-project" : ""}`}
    >
      {project}
    </button>
  ));
}


export default Dashboard;
