
const BASE_URL = 'http://localhost:5173/';

const getAuthHeaders = () => ({
  'Content-Type': 'application/json',
  Authorization: `Bearer ${localStorage.getItem('authToken')}`,
});
 

const safeJson = async (response) => {
  const text = await response.text();
  try {
    return JSON.parse(text);
  } catch {
    throw new Error(`Server returned non-JSON (status ${response.status}). Check the API URL and auth token.`);
  }
};
 

export const fetchProjects = async () => {
  const response = await fetch(`${BASE_URL}/projects`, {
    method: 'GET',
    headers: getAuthHeaders(),
  });
 
  const data = await safeJson(response);
 
  if (!response.ok) {
    throw new Error(data?.error || 'Failed to fetch projects.');
  }
 
  return data;
};
 

export const fetchTickets = async (projectId) => {
  const response = await fetch(`${BASE_URL}/projects/${projectId}/tickets`, {
    method: 'GET',
    headers: getAuthHeaders(),
  });
 
  const data = await safeJson(response);
 
  if (!response.ok) {
    throw new Error(data?.error || 'Failed to fetch tickets.');
  }
 
  return data;
};
 

export const fetchUsers = async (projectId) => {
  const response = await fetch(`${BASE_URL}/projects/${projectId}/users`, {
    method: 'GET',
    headers: getAuthHeaders(),
  });
 
  const data = await safeJson(response);
 
  if (!response.ok) {
    throw new Error(data?.error || 'Failed to fetch users.');
  }
 
  return data;
};