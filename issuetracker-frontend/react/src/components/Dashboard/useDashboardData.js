import { useState, useEffect } from 'react';
import { fetchProjects, fetchTickets, fetchUsers } from './dashboardServices';


const MOCK_PROJECTS = [
  { id: 0, name: 'Project Issue Tracker' },
  { id: 1, name: 'Marketing Platform' },
  { id: 2, name: 'Company CRM' },
  { id: 3, name: 'Internal Dashboard' },
];
 
const MOCK_TICKETS = [
  { id: 'KAN-1', title: 'Create login page',      assignee: 'John Doe',    priority: 'Medium', status: 'TODO'        },
  { id: 'KAN-2', title: 'Implement auth service', assignee: 'Sarah Smith', priority: 'High',   status: 'IN PROGRESS' },
  { id: 'KAN-3', title: 'Setup API integration',  assignee: 'Alex Johnson',priority: 'Low',    status: 'DONE'        },
];
 
const MOCK_USERS = ['John Doe', 'Sarah Smith', 'Alex Johnson', 'Emily Brown'];
 
export const useDashboardData = () => {
  const [selectedProjectId, setSelectedProjectId] = useState(0);

  const projects = MOCK_PROJECTS;
  const tickets  = MOCK_TICKETS;
  const users    = MOCK_USERS;
  const isLoading    = false;
  const errorMessage = '';
 
  // const [projects, setProjects] = useState([]);
  // const [selectedProjectId, setSelectedProjectId] = useState(null);
  // const [tickets, setTickets] = useState([]);
  // const [users, setUsers] = useState([]);
  // const [isLoading, setIsLoading] = useState(true);
  // const [errorMessage, setErrorMessage] = useState('');
 
  // useEffect(() => {
  //   const loadProjects = async () => {
  //     setIsLoading(true);
  //     setErrorMessage('');
 
  //     try {
  //       const data = await fetchProjects();
  //       setProjects(data);
  //       if (data.length > 0) {
  //         setSelectedProjectId(data[0].uuid); 
  //       }
  //     } catch (error) {
  //       setErrorMessage(error.message);
  //       setIsLoading(false);
  //     }
  //   };
 
  //   loadProjects();
  // }, []);
 
  // useEffect(() => {
  //   if (!selectedProjectId) return;
 
  //   const loadProjectData = async () => {
  //     setIsLoading(true);
  //     setErrorMessage('');
 
  //     try {
  //       const [ticketsData, usersData] = await Promise.all([
  //         fetchTickets(selectedProjectId),
  //         fetchUsers(selectedProjectId),
  //       ]);
 
  //       setTickets(ticketsData);
  //       setUsers(usersData);
  //     } catch (error) {
  //       setErrorMessage(error.message);
  //     } finally {
  //       setIsLoading(false);
  //     }
  //   };
 
  //   loadProjectData();
  // }, [selectedProjectId]);
 
  return {
    projects,
    tickets,
    users,
    selectedProjectId,
    setSelectedProjectId, 
    isLoading,
    errorMessage,
  };
};