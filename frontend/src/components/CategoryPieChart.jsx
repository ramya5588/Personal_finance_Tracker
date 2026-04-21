import { PieChart, Pie, Cell, Tooltip, Legend, ResponsiveContainer } from "recharts";

function CategoryPieChart({ data }) {
    const chartData = Array.isArray(data)
        ? data.map((item) => ({
            name: item.category,
            value: item.total
        }))
        : [];

    const COLORS = ["#3b82f6", "#10b981", "#f59e0b", "#ef4444", "#8b5cf6", "#14b8a6"];

    return (
        <div className="card chart-box">
            <h2 className="section-title">Expense Breakdown by Category</h2>

            {chartData.length > 0 ? (
                <ResponsiveContainer width="100%" height={280}>
                    <PieChart>
                        <Pie
                            data={chartData}
                            dataKey="value"
                            nameKey="name"
                            outerRadius={90}
                            label={false}
                        >
                            {chartData.map((entry, index) => (
                                <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                            ))}
                        </Pie>
                        <Tooltip />
                        <Legend />
                    </PieChart>
                </ResponsiveContainer>
            ) : (
                <p className="empty-text">No expense data available.</p>
            )}
        </div>
    );
}

export default CategoryPieChart;