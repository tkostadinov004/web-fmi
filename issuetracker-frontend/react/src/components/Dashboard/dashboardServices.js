import axios_client from "../../axiosClient";

export const fetchProjects = async () => {
  return await axios_client.get("/projects");
};

export const fetchTickets = async (projectId) => {
  return await axios_client.get(`/projects/${projectId}/tickets`);
};

export const fetchUsers = async (projectId) => {
  return await axios_client.get(`/projects/${projectId}/users`);
};
