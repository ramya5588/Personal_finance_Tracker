function InsightsPanel({ insights }) {
    return (
        <div className="card">
            <h2 className="section-title">AI Insights</h2>

            {insights && insights.length > 0 ? (
                <ul className="insight-list">
                    {insights.map((insight, index) => (
                        <li key={index}>{insight.message}</li>
                    ))}
                </ul>
            ) : (
                <p className="empty-text">No insights available yet.</p>
            )}
        </div>
    );
}

export default InsightsPanel;