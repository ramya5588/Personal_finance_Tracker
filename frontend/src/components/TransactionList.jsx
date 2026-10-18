import { deleteTransaction } from "../api/transactionApi";

function TransactionList({ transactions, onTransactionDeleted }) {
    const handleDelete = async (id) => {
        if (!window.confirm("Delete this transaction?")) {
            return;
        }

        try {
            await deleteTransaction(id);
            await onTransactionDeleted?.();
        } catch (error) {
            console.error("Error deleting transaction:", error);
            alert("Failed to delete transaction");
        }
    };

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
                        <th>Actions</th>
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
                                <td>
                                    <button
                                        className="delete-button"
                                        type="button"
                                        onClick={() => handleDelete(transaction.id)}
                                    >
                                        Delete
                                    </button>
                                </td>
                            </tr>
                        ))
                    ) : (
                        <tr>
                            <td colSpan="8">No transactions found</td>
                        </tr>
                    )}
                    </tbody>
                </table>
            </div>
        </div>
    );
}

export default TransactionList;
