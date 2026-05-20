const API_BASE = "http://localhost:8080";

export const authService = {
  login: async (username, password) => {
    const response = await fetch(`${API_BASE}/auth/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ username, password }),
    });

    const authToken = response.headers.get("Authorization");

    if (!response.ok || !authToken) {
      throw new Error("Възникна грешка при влизане.");
    }
    return authToken;
  },

  // register:
  // logout:
};
