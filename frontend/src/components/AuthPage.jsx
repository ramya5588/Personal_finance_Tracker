import { useState } from "react";
import { login, register } from "../api/authApi";

function AuthPage({ onAuthenticated }) {
    const [mode, setMode] = useState("login");
    const [formData, setFormData] = useState({ name: "", email: "", password: "" });
    const [error, setError] = useState("");
    const [submitting, setSubmitting] = useState(false);

    const handleChange = (event) => {
        const { name, value } = event.target;
        setFormData((current) => ({ ...current, [name]: value }));
    };

    const switchMode = () => {
        setMode((current) => current === "login" ? "register" : "login");
        setError("");
    };

    const handleSubmit = async (event) => {
        event.preventDefault();
        setError("");
        setSubmitting(true);

        try {
            const response = mode === "register"
                ? await register(formData)
                : await login({ email: formData.email, password: formData.password });
            onAuthenticated(response);
        } catch (requestError) {
            const status = requestError.response?.status;
            const message = requestError.response?.data?.detail;
            setError(message ?? (status === 401 ? "Invalid email or password." : "Unable to continue. Please try again."));
        } finally {
            setSubmitting(false);
        }
    };

    return (
        <main className="auth-page">
            <section className="auth-card">
                <div className="auth-brand">FT</div>
                <p className="auth-eyebrow">Personal Finance Tracker</p>
                <h1>{mode === "login" ? "Welcome back" : "Create your account"}</h1>
                <p className="auth-subtitle">
                    {mode === "login"
                        ? "Sign in to view your private financial dashboard."
                        : "Start tracking income and expenses securely."}
                </p>

                <form onSubmit={handleSubmit}>
                    {mode === "register" && (
                        <div className="form-group">
                            <label className="form-label" htmlFor="name">Name</label>
                            <input id="name" className="form-input" name="name" value={formData.name}
                                   onChange={handleChange} autoComplete="name" maxLength="100" required />
                        </div>
                    )}
                    <div className="form-group">
                        <label className="form-label" htmlFor="email">Email</label>
                        <input id="email" className="form-input" type="email" name="email" value={formData.email}
                               onChange={handleChange} autoComplete="email" required />
                    </div>
                    <div className="form-group">
                        <label className="form-label" htmlFor="password">Password</label>
                        <input id="password" className="form-input" type="password" name="password"
                               value={formData.password} onChange={handleChange}
                               autoComplete={mode === "login" ? "current-password" : "new-password"}
                               minLength={mode === "register" ? 8 : undefined} required />
                        {mode === "register" && <span className="field-hint">Use at least 8 characters.</span>}
                    </div>

                    {error && <div className="auth-error" role="alert">{error}</div>}
                    <button className="form-button auth-submit" type="submit" disabled={submitting}>
                        {submitting ? "Please wait…" : mode === "login" ? "Sign in" : "Create account"}
                    </button>
                </form>

                <button className="auth-switch" type="button" onClick={switchMode}>
                    {mode === "login" ? "New here? Create an account" : "Already have an account? Sign in"}
                </button>
            </section>
        </main>
    );
}

export default AuthPage;
