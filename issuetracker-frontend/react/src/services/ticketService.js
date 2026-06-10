import axios_client from "../axiosClient";

export const ticketService = {
  getTicket: async (projectId, ticketCode) => {
    return await axios_client.get(`/projects/${projectId}/tickets/${ticketCode}`);
  },

  updateTicket: async (projectId, ticketCode, data) => {
    return await axios_client.patch(`/projects/${projectId}/tickets/${ticketCode}`, data);
  },

  getComments: async (projectId, ticketCode) => {
    return await axios_client.get(`/projects/${projectId}/tickets/${ticketCode}/comments?page_number=1&page_size=50`);
  },

  addComment: async (projectId, ticketCode, content) => {
    return await axios_client.post(`/projects/${projectId}/tickets/${ticketCode}/comments`, { content });
  },

  updateAssignee: async (projectId, ticketCode, username) => {
    if (username === "") {
      return await axios_client.delete(`/projects/${projectId}/tickets/${ticketCode}/assignee`);
    }

    return await axios_client.patch(`/projects/${projectId}/tickets/${ticketCode}/assignee?assigneeUsername=${username}`);
  },

  updateComment: async (commentUuid, content) => {
    return await axios_client.patch(`/comments/${commentUuid}`, { content });
  },

  deleteComment: async (commentUuid) => {
    try {
      return await axios_client.delete(`/comments/${commentUuid}`);
    } catch (error) {
      const err = new Error("Грешка при изтриване на коментара");
      err.status = error.response?.status;
      throw err;
    }
  },

  getUsers: async (projectUuid) => {
    return await axios_client.get(`/projects/${projectUuid}/users`);
  },
};
