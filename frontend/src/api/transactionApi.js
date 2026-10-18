import { apiClient } from "./apiClient";

export const getAllTransactions = async () => {
    const response = await apiClient.get("/transactions");
    return response.data;
};

export const createTransaction = async (transactionData) => {
    const response = await apiClient.post("/transactions", transactionData);
    return response.data;
};

export const getDashboardSummary = async () => {
    const response = await apiClient.get("/dashboard/summary");
    return response.data;
};

export const getCategoryBreakdown = async () => {
    const response = await apiClient.get("/dashboard/category-breakdown");
    return response.data;
};

export const uploadTransactionsCsv = async (file) => {
    const formData = new FormData();
    formData.append("file", file);

    const response = await apiClient.post("/transactions/upload", formData, {
        headers: {
            "Content-Type": "multipart/form-data"
        }
    });

    return response.data;
};

export const getMonthlyTrend = async () => {
    const response = await apiClient.get("/dashboard/monthly-trend");
    return response.data;
};

export const getInsights = async () => {
    const response = await apiClient.get("/dashboard/insights");
    return response.data;
};

export const deleteTransaction = async (id) => {
    await apiClient.delete(`/transactions/${id}`);
};
