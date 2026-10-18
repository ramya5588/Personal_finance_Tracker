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
import AuthPage from "./components/AuthPage";
import { clearSession, getStoredSession, saveSession } from "./api/apiClient";
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
    const [user, setUser] = useState(getStoredSession);
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
        if (!user) {
            return undefined;
        }

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
    }, [user]);

    useEffect(() => {
        const handleExpiredSession = () => setUser(null);
        window.addEventListener("finance-auth-expired", handleExpiredSession);
        return () => window.removeEventListener("finance-auth-expired", handleExpiredSession);
    }, []);

    const handleAuthenticated = (response) => {
        setUser(saveSession(response));
    };

    const handleLogout = () => {
        clearSession();
        setUser(null);
        setSummary(EMPTY_SUMMARY);
        setTransactions([]);
        setCategoryData([]);
        setMonthlyTrendData([]);
        setInsights([]);
    };

    if (!user) {
        return <AuthPage onAuthenticated={handleAuthenticated} />;
    }

    return (
        <div className="app-container">
            <header className="app-header">
                <div>
                    <p className="app-eyebrow">Personal Finance Tracker</p>
                    <h1 className="app-title">Your financial dashboard</h1>
                </div>
                <div className="user-menu">
                    <div>
                        <strong>{user.name}</strong>
                        <span>{user.email}</span>
                    </div>
                    <button className="logout-button" type="button" onClick={handleLogout}>Log out</button>
                </div>
            </header>

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
