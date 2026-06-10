import axios_client from "../axiosClient";

export const authService = {
  login: async (username, password) => {
    const response = await axios_client.post(`/auth/login`, { username, password }, { rawResponse: true });

    const accessToken = response.headers?.Authorization || response.headers?.authorization;

    if (response.status !== 200 || !accessToken) {
      throw new Error("Възникна грешка при влизане.");
    }
    return accessToken;
  },

  // register:
  // logout:
};
