import { apiClient } from "./apiClient";

export const register = async (registrationData) => {
    const response = await apiClient.post("/auth/register", registrationData);
    return response.data;
};

export const login = async (credentials) => {
    const response = await apiClient.post("/auth/login", credentials);
    return response.data;
};

export const getCurrentUser = async () => {
    const response = await apiClient.get("/auth/me");
    return response.data;
};
