import axios_client from "../axiosClient";

export const authService = {
  login: async (username, password) => {
    const response = await axios_client.post(`/auth/login`, { username, password }, { rawResponse: true });

    const authToken = response.headers?.Authorization || response.headers?.authorization;

    if (response.status !== 200 || !authToken) {
      throw new Error("Възникна грешка при влизане.");
    }
    return authToken;
  },

  // register:
  // logout:
};
