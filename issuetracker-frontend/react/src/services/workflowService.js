import axios_client from "../axiosClient";

export const workflowService = {
  getWorkflow: async (projectId) => {
    return await axios_client.get(`/projects/${projectId}/workflow`);
  },

  createWorkflow: async (projectId, workflowData) => {
    return await axios_client.post(`/projects/${projectId}/workflow`, workflowData);
  },

  updateWorkflow: async (projectId, workflowData) => {
    return await axios_client.put(`/projects/${projectId}/workflow`, workflowData);
  },

  deleteWorkflow: async (projectId) => {
    return await axios_client.delete(`/projects/${projectId}/workflow`);
  },
};
