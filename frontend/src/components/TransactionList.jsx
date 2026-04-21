function TransactionList({ transactions }) {
    return (
        <div className="card">
            <h2 className="section-title">All Transactions</h2>

            <div className="table-wrapper">
                <table className="transaction-table">
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Amount</th>
                        <th>Type</th>
                        <th>Category</th>
                        <th>Description</th>
                        <th>Date</th>
                        <th>Source</th>
                    </tr>
                    </thead>
                    <tbody>
                    {transactions.length > 0 ? (
                        transactions.map((transaction) => (
                            <tr key={transaction.id}>
                                <td>{transaction.id}</td>
                                <td>{transaction.amount}</td>
                                <td>{transaction.type}</td>
                                <td>{transaction.category}</td>
                                <td>{transaction.description}</td>
                                <td>{transaction.transactionDate}</td>
                                <td>{transaction.source}</td>
                            </tr>
                        ))
                    ) : (
                        <tr>
                            <td colSpan="7">No transactions found</td>
                        </tr>
                    )}
                    </tbody>
                </table>
            </div>
        </div>
    );
}

export default TransactionList;