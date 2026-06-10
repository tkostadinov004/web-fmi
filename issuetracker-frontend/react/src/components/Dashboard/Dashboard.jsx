import React, { useEffect, useState } from "react";
import { NavLink, useParams } from "react-router-dom";
import "./Dashboard.css";

import { ProjectSidebar } from "./ProjectSidebar";
import { UserAvatarList } from "./UserAvatarList";
import { FilterMenu } from "./FilterMenu";
import { TicketTable } from "./TicketTable";
import { useDashboardData } from "./useDashboardData";
import Navbar from "../homepage/navbar/Navbar";
import WorkflowEditor from "../TicketDetail/WorkflowEditor";
import { projectService } from "../../services/projectService";

const Dashboard = () => {
  const {projectUuid} = useParams();
  const { projects,
    tickets,
    users,
    isInitialLoading,
    isProjectLoading,
    errorMessage } = useDashboardData(projectUuid);  
  
  
  const [searchQuery, setSearchQuery] = useState('');
  const [activeFilter, setActiveFilter] = useState('assignee');
  const [selectedFilters, setSelectedFilters] = useState([]);
  const [isWorkflowOpen, setIsWorkflowOpen] = useState(false);
  const [canEditWorkflow, setCanEditWorkflow] = useState(false);

  const filterOptions =
    activeFilter === 'assignee'
      ? [...new Set(tickets.map((t) => t.assignee?.username).filter(Boolean))]
      : [...new Set(tickets.map((t) => t.ticketStatus).filter(Boolean))];
 
  const visibleTickets = tickets.filter((ticket) => {
    const matchesSearch =
      searchQuery.trim() === '' ||
      ticket.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
      ticket.code.toLowerCase().includes(searchQuery.toLowerCase());
 
    const matchesFilter =
      selectedFilters.length === 0 ||
      (activeFilter === 'assignee'
        ? selectedFilters.includes(ticket.assignee?.username)
        : selectedFilters.includes(ticket.ticketStatus));
 
    return matchesSearch && matchesFilter;
  });

  const handleFilterChange = (value) => {
    setSelectedFilters((prev) =>
      prev.includes(value) ? prev.filter((v) => v !== value) : [...prev, value]
    );
  };
 
  const handleFilterTypeChange = (type) => {
    setActiveFilter(type);
    setSelectedFilters([]);
  };
 
  const selectedProject = projects.find((p) => p.uuid === projectUuid);

  useEffect(() => {
    const fetchProjectPermissions = async () => {
      if (!projectUuid) {
        setCanEditWorkflow(false);
        return;
      }

      try {
        const projectData = await projectService.getProject(projectUuid);
        const username = localStorage.getItem('currentUsername');

        const isCreator = projectData.creator?.username === username;
        const myUserObj = projectData.users?.find((u) => u.username === username);
        const hasAdminRole = myUserObj?.roles?.includes('TEAM_LEAD');

        setCanEditWorkflow(isCreator || hasAdminRole);
      } catch (error) {
        console.error('Грешка при проверка на правата за workflow:', error);
        setCanEditWorkflow(false);
      }
    };

    fetchProjectPermissions();
  }, [projectUuid]);

  if (isInitialLoading) {
    return <div className="dashboard-loading">Loading...</div>;
  }
 
  if (errorMessage) {
    return <div className="dashboard-error">{errorMessage}</div>;
  }
  return (
    <div className="dashboard-layout">
      <ProjectSidebar
        projects={projects}
        selectedProjectId={projectUuid}
        onCreateProject={() => console.log('Create project clicked')}
      />
 
      <main className="main-content">
        <div>
          <Navbar></Navbar>
        </div>
        <div className="top-header">
          <div className="project-title-wrapper">
            <div className="project-title-row">
              <h2 className="project-title">{selectedProject?.name ?? '—'}</h2>
              {projectUuid && (
                <button
                  className="workflow-placeholder-btn"
                  type="button"
                  onClick={() => setIsWorkflowOpen(true)}
                  title="Управление на workflow-a"
                >
                  <i className="fa-solid fa-code-branch"></i> Workflow
                </button>
              )}
            </div>
            <p className="project-subtitle">Current tickets and team members</p>
          </div>
          <UserAvatarList users={users} />
        </div>
 
        <div className="table-menus">
          <input
            type="search"
            placeholder="Search..."
            name="taskSearch"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
 
          <FilterMenu
            options={filterOptions}
            activeFilter={activeFilter}
            selectedFilters={selectedFilters}
            onFilterTypeChange={handleFilterTypeChange}
            onFilterChange={handleFilterChange}
          />
        </div>
 
        {isProjectLoading ? <div className="dashboard-loading">Loading...</div> : <TicketTable tickets={visibleTickets}  projectUuid={projectUuid}/>}
      </main>

      {isWorkflowOpen && (
        <div className="workflow-modal-overlay">
          <div className="workflow-modal-content">
            <WorkflowEditor
              projectId={projectUuid}
              canEdit={canEditWorkflow}
              onClose={() => setIsWorkflowOpen(false)}
            />
          </div>
        </div>
      )}
    </div>
  );
};

export default Dashboard;
