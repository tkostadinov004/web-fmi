import React, { use, useState } from "react";
import { NavLink, useNavigate } from "react-router-dom";
import "./Dashboard.css";

import { ProjectSidebar } from "./ProjectSidebar";
import { UserAvatarList } from "./UserAvatarList";
import { FilterMenu } from "./FilterMenu";
import { TicketTable } from "./TicketTable";
import {useDashboardData} from "./useDashboardData";


const Dashboard = () => {
  const { projects,
    tickets,
    users,
    selectedProjectId,
    setSelectedProjectId, 
    isLoading,
    errorMessage, } = useDashboardData();  
  
  const [searchQuery, setSearchQuery] = useState('');
  const [activeFilter, setActiveFilter] = useState('assignee');
  const [selectedFilters, setSelectedFilters] = useState([]);
 
  const filterOptions =
    activeFilter === 'assignee'
      ? [...new Set(tickets.map((t) => t.assignee))]
      : [...new Set(tickets.map((t) => t.status))];
 
  const visibleTickets = tickets.filter((ticket) => {
    const matchesSearch =
      searchQuery.trim() === '' ||
      ticket.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
      ticket.id.toLowerCase().includes(searchQuery.toLowerCase());
 
    const matchesFilter =
      selectedFilters.length === 0 ||
      (activeFilter === 'assignee'
        ? selectedFilters.includes(ticket.assignee)
        : selectedFilters.includes(ticket.status));
 
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
 
  // При смяна на проект — нулираме търсенето и филтрите
  const handleProjectSelect = (projectId) => {
    setSelectedProjectId(projectId);
    setSearchQuery('');
    setSelectedFilters([]);
  };
 
  const selectedProject = projects.find((p) => p.id === selectedProjectId);
 
  if (isLoading) {
    return <div className="dashboard-loading">Loading...</div>;
  }
 
  if (errorMessage) {
    return <div className="dashboard-error">{errorMessage}</div>;
  }
 
  return (
    <div className="dashboard-layout">
      <ProjectSidebar
        projects={projects}
        selectedProjectId={selectedProjectId}
        onProjectSelect={handleProjectSelect}
        onCreateProject={() => console.log('Create project clicked')}
      />
 
      <main className="main-content">
        <div className="top-header">
          <div>
            <h2 className="project-title">{selectedProject?.name ?? '—'}</h2>
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
 
        <TicketTable tickets={visibleTickets} />
      </main>
    </div>
  );
};

export default Dashboard;
