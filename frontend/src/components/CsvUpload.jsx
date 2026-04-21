import { useState } from "react";
import { uploadTransactionsCsv } from "../api/transactionApi";

function CsvUpload({ onUploadSuccess }) {
    const [file, setFile] = useState(null);

    const handleUpload = async () => {
        if (!file) {
            alert("Please choose a CSV file");
            return;
        }

        try {
            await uploadTransactionsCsv(file);

            alert("CSV uploaded successfully");
            setFile(null);

            if (onUploadSuccess) {
                onUploadSuccess();
            }
        } catch (error) {
            console.error("Error uploading CSV:", error);
            alert("Failed to upload CSV");
        }
    };

    return (
        <div className="card">
            <h2 className="section-title">Upload CSV</h2>

            <div className="form-group">
                <input
                    className="form-file"
                    type="file"
                    accept=".csv"
                    onChange={(e) => setFile(e.target.files[0])}
                />
            </div>

            <button
                className="form-button upload-button"
                type="button"
                onClick={handleUpload}
            >
                Upload CSV
            </button>
        </div>
    );
}

export default CsvUpload;