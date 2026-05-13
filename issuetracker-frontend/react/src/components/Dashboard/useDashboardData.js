import { useState, useEffect } from 'react';
import { fetchProjects, fetchTickets, fetchUsers } from './dashboardServices';


const MOCK_PROJECTS = [
  { uuid: 'proj-1', name: 'Project Issue Tracker' },
  { uuid: 'proj-2', name: 'Marketing Platform' },
  { uuid: 'proj-3', name: 'Company CRM' },
  { uuid: 'proj-4', name: 'Internal Dashboard' },
];
 
const MOCK_TICKETS = [
  { code: 'KAN-1', title: 'Create login page',      assignee: { username: 'John Doe'},    ticketPriority: 'Medium', ticketStatus: 'TODO'        },
  { code: 'KAN-2', title: 'Implement auth service', assignee: { username: 'Sarah Smith'}, ticketPriority: 'High',   ticketStatus: 'IN PROGRESS' },
  { code: 'KAN-3', title: 'Setup API integration',  assignee: { username: 'Alex Johnson'},ticketPriority: 'Low',    ticketStatus: 'DONE'        },
];
 
const MOCK_USERS = [
  { username: 'John Doe',    roles: ['DEVELOPER'], profilePicturePath: '' },
  { username: 'Sarah Smith', roles: ['DEVELOPER'], profilePicturePath: '' },
  { username: 'Alex Johnson',roles: ['DEVELOPER'], profilePicturePath: '' },
  { username: 'Emily Brown', roles: ['DEVELOPER'], profilePicturePath: '' },
];
 
export const useDashboardData = () => {
  // const [selectedProjectId, setSelectedProjectId] = useState(MOCK_PROJECTS[0].uuid);

  // const projects = MOCK_PROJECTS;
  // const tickets  = MOCK_TICKETS;
  // const users    = MOCK_USERS;
  // const isLoading    = false;
  // const errorMessage = '';
 
  const [projects, setProjects] = useState([]);
  const [selectedProjectId, setSelectedProjectId] = useState(null);
  const [tickets, setTickets] = useState([]);
  const [users, setUsers] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [errorMessage, setErrorMessage] = useState('');
 
  useEffect(() => {
    const loadProjects = async () => {
      setIsLoading(true);
      setErrorMessage('');
 
      try {
        const data = await fetchProjects();
        setProjects(data);
        if (data.length > 0) {
          setSelectedProjectId(data[0].uuid); 
        }
      } catch (error) {
        setErrorMessage(error.message);
      } finally {  
        setIsLoading(false);
      }
    };
 
    loadProjects();
  }, []);
 
  useEffect(() => {
    if (!selectedProjectId) return;
 
    const loadProjectData = async () => {
      setIsLoading(true);
      setErrorMessage('');
 
      try {
        const [ticketsData, usersData] = await Promise.all([
          fetchTickets(selectedProjectId),
          fetchUsers(selectedProjectId),
        ]);
 
        setTickets(ticketsData);
        setUsers(usersData);
      } catch (error) {
        setErrorMessage(error.message);
      } finally {
        setIsLoading(false);
      }
    };
 
    loadProjectData();
  }, [selectedProjectId]);
 
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