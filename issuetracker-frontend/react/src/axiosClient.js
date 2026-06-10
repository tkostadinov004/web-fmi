import axios from "axios";
import toastr from "./services/toastrClient";

const api_base = import.meta.env.VITE_API_BASE;
let refresh_promise = null;

const axios_config = {
  baseURL: api_base,
  headers: {
    "Content-Type": "application/json",
  },
  withCredentials: true,
};
const axios_client = axios.create(axios_config);

axios_client.interceptors.request.use((config) => {
  const token = localStorage.getItem("accessToken");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

async function refresh_token() {
  if (!refresh_promise) {
    refresh_promise = axios
      .post("/auth/refresh", null, { ...axios_config, rawResponse: true })
      .then((response) => {
        localStorage.setItem("accessToken", response.headers?.Authorization || response.headers?.authorization);
      })
      .catch((err) => {
        toastr.error(`Refresh failed: ${err?.message || String(err)}`);
        localStorage.removeItem("accessToken");
        window.location.href = "/login";
      })
      .finally(() => {
        refresh_promise = null;
      });
  }

  return refresh_promise;
}

axios_client.interceptors.response.use(
  (response) => {
    if (response.config?.rawResponse) {
      return response;
    }
    return response.data;
  },
  async (error) => {
    if (error.response.status == 401) {
      if (error.response.data.tokenExpired === true) {
        const refresh_token_fetch = await refresh_token();
        return axios_client(error.config);
      } else {
        window.location.href = "/login";
      }
    }

    return Promise.reject(error);
  }
);

export default axios_client;
