import {
    LineChart,
    Line,
    XAxis,
    YAxis,
    CartesianGrid,
    Tooltip,
    Legend,
    ResponsiveContainer
} from "recharts";

function MonthlyTrendChart({ data }) {
    const chartData = Array.isArray(data) ? data : [];

    return (
        <div className="card chart-box">
            <h2 className="section-title">Monthly Income vs Expense Trend</h2>

            {chartData.length > 0 ? (
                <ResponsiveContainer width="100%" height={300}>
                    <LineChart data={chartData}>
                        <CartesianGrid strokeDasharray="3 3" />
                        <XAxis dataKey="month" />
                        <YAxis />
                        <Tooltip />
                        <Legend />
                        <Line type="monotone" dataKey="income" stroke="#16a34a" strokeWidth={3} />
                        <Line type="monotone" dataKey="expense" stroke="#dc2626" strokeWidth={3} />
                    </LineChart>
                </ResponsiveContainer>
            ) : (
                <p className="empty-text">No monthly trend data available.</p>
            )}
        </div>
    );
}

export default MonthlyTrendChart;