import { useState } from "react";
import { createTransaction } from "../api/transactionApi";

function TransactionForm({ onTransactionAdded }) {
    const [formData, setFormData] = useState({
        amount: "",
        type: "EXPENSE",
        category: "",
        description: "",
        transactionDate: "",
        source: "MANUAL"
    });

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData((prev) => ({
            ...prev,
            [name]: value
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        try {
            await createTransaction({
                ...formData,
                amount: Number(formData.amount)
            });

            setFormData({
                amount: "",
                type: "EXPENSE",
                category: "",
                description: "",
                transactionDate: "",
                source: "MANUAL"
            });

            if (onTransactionAdded) {
                onTransactionAdded();
            }
        } catch (error) {
            console.error("Error adding transaction:", error);
            alert("Failed to add transaction");
        }
    };

    return (
        <div className="card">
            <h2 className="section-title">Add Transaction</h2>

            <form onSubmit={handleSubmit}>
                <div className="form-group">
                    <input
                        className="form-input"
                        type="number"
                        name="amount"
                        placeholder="Amount"
                        value={formData.amount}
                        onChange={handleChange}
                        required
                    />
                </div>

                <div className="form-group">
                    <select
                        className="form-select"
                        name="type"
                        value={formData.type}
                        onChange={handleChange}
                    >
                        <option value="EXPENSE">EXPENSE</option>
                        <option value="INCOME">INCOME</option>
                    </select>
                </div>

                <div className="form-group">
                    <input
                        className="form-input"
                        type="text"
                        name="category"
                        placeholder="Category"
                        value={formData.category}
                        onChange={handleChange}
                        required
                    />
                </div>

                <div className="form-group">
                    <input
                        className="form-input"
                        type="text"
                        name="description"
                        placeholder="Description"
                        value={formData.description}
                        onChange={handleChange}
                    />
                </div>

                <div className="form-group">
                    <input
                        className="form-input"
                        type="date"
                        name="transactionDate"
                        value={formData.transactionDate}
                        onChange={handleChange}
                        required
                    />
                </div>

                <button className="form-button" type="submit">
                    Add Transaction
                </button>
            </form>
        </div>
    );
}

export default TransactionForm;