import axios_client from "../axiosClient";

export const projectService = {
  getProject: async (projectId) => {
    return await axios_client.get(`/projects/${projectId}`);
  },
};
