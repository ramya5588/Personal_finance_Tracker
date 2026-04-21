import axios from "axios";

const BASE_URL = "http://localhost:8080/api";

export const getAllTransactions = async () => {
    const response = await axios.get(`${BASE_URL}/transactions`);
    return response.data;
};

export const createTransaction = async (transactionData) => {
    const response = await axios.post(`${BASE_URL}/transactions`, transactionData);
    return response.data;
};

export const getDashboardSummary = async () => {
    const response = await axios.get(`${BASE_URL}/dashboard/summary`);
    return response.data;
};

export const getCategoryBreakdown = async () => {
    const response = await axios.get(`${BASE_URL}/dashboard/category-breakdown`);
    return response.data;
};

export const uploadTransactionsCsv = async (file) => {
    const formData = new FormData();
    formData.append("file", file);

    const response = await axios.post(`${BASE_URL}/transactions/upload`, formData, {
        headers: {
            "Content-Type": "multipart/form-data"
        }
    });

    return response.data;
};

export const getMonthlyTrend = async () => {
    const response = await axios.get(`${BASE_URL}/dashboard/monthly-trend`);
    return response.data;
};

export const getInsights = async () => {
    const response = await axios.get(`${BASE_URL}/dashboard/insights`);
    return response.data;
};