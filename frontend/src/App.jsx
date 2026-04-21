import { useEffect, useState } from "react";
import {
    getAllTransactions,
    getDashboardSummary,
    getCategoryBreakdown,
    getMonthlyTrend,
    getInsights
} from "./api/transactionApi";
import DashboardSummary from "./components/DashboardSummary";
import TransactionForm from "./components/TransactionForm";
import TransactionList from "./components/TransactionList";
import CategoryPieChart from "./components/CategoryPieChart";
import CsvUpload from "./components/CsvUpload";
import MonthlyTrendChart from "./components/MonthlyTrendChart";
import InsightsPanel from "./components/InsightsPanel";
import "./App.css";

function App() {
    const [summary, setSummary] = useState({
        totalIncome: 0,
        totalExpense: 0,
        netBalance: 0
    });

    const [transactions, setTransactions] = useState([]);
    const [categoryData, setCategoryData] = useState([]);
    const [monthlyTrendData, setMonthlyTrendData] = useState([]);
    const [insights, setInsights] = useState([]);

    const loadSummary = async () => {
        try {
            const data = await getDashboardSummary();
            setSummary(data);
        } catch (error) {
            console.error("Error fetching dashboard summary:", error);
            setSummary({ totalIncome: 0, totalExpense: 0, netBalance: 0 });
        }
    };

    const loadTransactions = async () => {
        try {
            const data = await getAllTransactions();
            setTransactions(Array.isArray(data) ? data : []);
        } catch (error) {
            console.error("Error fetching transactions:", error);
            setTransactions([]);
        }
    };

    const loadCategoryBreakdown = async () => {
        try {
            const data = await getCategoryBreakdown();
            setCategoryData(Array.isArray(data) ? data : []);
        } catch (error) {
            console.error("Error fetching category breakdown:", error);
            setCategoryData([]);
        }
    };

    const loadMonthlyTrend = async () => {
        try {
            const data = await getMonthlyTrend();
            setMonthlyTrendData(Array.isArray(data) ? data : []);
        } catch (error) {
            console.error("Error fetching monthly trend:", error);
            setMonthlyTrendData([]);
        }
    };

    const loadInsights = async () => {
        try {
            const data = await getInsights();
            setInsights(Array.isArray(data) ? data : []);
        } catch (error) {
            console.error("Error fetching insights:", error);
            setInsights([]);
        }
    };

    const refreshData = async () => {
        await Promise.all([
            loadSummary(),
            loadTransactions(),
            loadCategoryBreakdown(),
            loadMonthlyTrend(),
            loadInsights()
        ]);
    };

    useEffect(() => {
        refreshData();
    }, []);

    return (
        <div className="app-container">
            <h1 className="app-title">Personal Finance Tracker</h1>

            <DashboardSummary summary={summary} />

            <div className="content-grid">
                <div className="left-panel">
                    <TransactionForm onTransactionAdded={refreshData} />
                    <CsvUpload onUploadSuccess={refreshData} />
                    <CategoryPieChart data={categoryData} />
                    <MonthlyTrendChart data={monthlyTrendData} />
                    <InsightsPanel insights={insights} />
                </div>

                <div className="right-panel">
                    <TransactionList transactions={transactions} />
                </div>
            </div>
        </div>
    );
}

export default App;