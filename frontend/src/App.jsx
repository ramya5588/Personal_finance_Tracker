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

const EMPTY_SUMMARY = { totalIncome: 0, totalExpense: 0, netBalance: 0 };

const requestWithFallback = async (request, fallback, label) => {
    try {
        return await request();
    } catch (error) {
        console.error(`Error fetching ${label}:`, error);
        return fallback;
    }
};

const fetchDashboardData = async () => {
    const [summary, transactions, categoryData, monthlyTrendData, insights] = await Promise.all([
        requestWithFallback(getDashboardSummary, EMPTY_SUMMARY, "dashboard summary"),
        requestWithFallback(getAllTransactions, [], "transactions"),
        requestWithFallback(getCategoryBreakdown, [], "category breakdown"),
        requestWithFallback(getMonthlyTrend, [], "monthly trend"),
        requestWithFallback(getInsights, [], "insights")
    ]);

    return {
        summary,
        transactions: Array.isArray(transactions) ? transactions : [],
        categoryData: Array.isArray(categoryData) ? categoryData : [],
        monthlyTrendData: Array.isArray(monthlyTrendData) ? monthlyTrendData : [],
        insights: Array.isArray(insights) ? insights : []
    };
};

function App() {
    const [summary, setSummary] = useState(EMPTY_SUMMARY);

    const [transactions, setTransactions] = useState([]);
    const [categoryData, setCategoryData] = useState([]);
    const [monthlyTrendData, setMonthlyTrendData] = useState([]);
    const [insights, setInsights] = useState([]);

    const refreshData = async () => {
        const data = await fetchDashboardData();
        setSummary(data.summary);
        setTransactions(data.transactions);
        setCategoryData(data.categoryData);
        setMonthlyTrendData(data.monthlyTrendData);
        setInsights(data.insights);
    };

    useEffect(() => {
        let cancelled = false;

        fetchDashboardData().then((data) => {
            if (!cancelled) {
                setSummary(data.summary);
                setTransactions(data.transactions);
                setCategoryData(data.categoryData);
                setMonthlyTrendData(data.monthlyTrendData);
                setInsights(data.insights);
            }
        });

        return () => {
            cancelled = true;
        };
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
                    <TransactionList transactions={transactions} onTransactionDeleted={refreshData} />
                </div>
            </div>
        </div>
    );
}

export default App;
