function DashboardSummary({ summary }) {
    return (
        <div className="summary-grid">
            <div className="summary-card">
                <h3>Total Income</h3>
                <p className="income-text">{summary.totalIncome}</p>
            </div>

            <div className="summary-card">
                <h3>Total Expense</h3>
                <p className="expense-text">{summary.totalExpense}</p>
            </div>

            <div className="summary-card">
                <h3>Net Balance</h3>
                <p className="balance-text">{summary.netBalance}</p>
            </div>
        </div>
    );
}

export default DashboardSummary;