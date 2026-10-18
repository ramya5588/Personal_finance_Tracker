import axios from "axios";

const BASE_URL = import.meta.env.VITE_API_BASE_URL ?? "/api";
const TOKEN_KEY = "finance_tracker_token";

export const apiClient = axios.create({
    baseURL: BASE_URL
});

apiClient.interceptors.request.use((config) => {
    const token = localStorage.getItem(TOKEN_KEY);
    const isPublicAuthRequest = config.url === "/auth/login" || config.url === "/auth/register";
    if (token && !isPublicAuthRequest) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

apiClient.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response?.status === 401 && localStorage.getItem(TOKEN_KEY)) {
            localStorage.removeItem(TOKEN_KEY);
            localStorage.removeItem("finance_tracker_user");
            window.dispatchEvent(new Event("finance-auth-expired"));
        }
        return Promise.reject(error);
    }
);

export const saveSession = (authResponse) => {
    const user = { name: authResponse.name, email: authResponse.email };
    localStorage.setItem(TOKEN_KEY, authResponse.token);
    localStorage.setItem("finance_tracker_user", JSON.stringify(user));
    return user;
};

export const clearSession = () => {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem("finance_tracker_user");
};

export const getStoredSession = () => {
    const token = localStorage.getItem(TOKEN_KEY);
    const storedUser = localStorage.getItem("finance_tracker_user");
    if (!token || !storedUser) {
        return null;
    }

    try {
        return JSON.parse(storedUser);
    } catch {
        clearSession();
        return null;
    }
};
