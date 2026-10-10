import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import api from "../api";
import { useAuth } from "../AuthContext";

function Login() {
  const navigate = useNavigate();
  const { login } = useAuth();
  const [form, setForm] = useState({ email: "", password: "" });
  const [error, setError] = useState("");
  const [submitting, setSubmitting] = useState(false);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  async function submit(e) {
    e.preventDefault();
    setError("");
    setSubmitting(true);
    try {
      const res = await api.post("/auth/login", form);
      login(res.data);
      navigate("/app");
    } catch (err) {
      if (err.response?.status === 423) {
        setError(err.response.data.error || "This account is locked. Contact an admin.");
      } else {
        setError(err.response?.data?.error || "Something went wrong. Please try again.");
      }
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div style={{ display: "flex", minHeight: "100vh" }}>
      <div style={{ flex: "1 1 46%", background: "linear-gradient(160deg, var(--espresso) 0%, var(--espresso-dark) 100%)", color: "var(--parchment)", display: "flex", flexDirection: "column", justifyContent: "center", padding: "60px" }}>
        <div style={{ maxWidth: "440px" }}>
          <div style={{ fontSize: "13px", letterSpacing: "0.08em", color: "var(--brass-light)", marginBottom: "18px" }}>Smart Library</div>
          <h1 style={{ fontFamily: "var(--font-display)", fontSize: "42px", lineHeight: "1.15", color: "#fff", fontWeight: 700, marginBottom: "20px" }}>Every book finds its reader.</h1>
          <p style={{ color: "var(--parchment)", fontSize: "15.5px", lineHeight: "1.6", opacity: 0.88 }}>Browse the catalog, borrow what you need, and return it when you're done. Staff can manage the shelves and keep an eye on account activity from the same place.</p>
        </div>
      </div>

      <div style={{ flex: "1 1 54%", display: "flex", alignItems: "center", justifyContent: "center", padding: "40px", background: "var(--paper)" }}>
        <form onSubmit={submit} style={{ width: "100%", maxWidth: "360px" }}>
          <h2 style={{ fontSize: "24px", marginBottom: "6px" }}>Sign in</h2>
          <p className="help-text" style={{ marginBottom: "28px" }}>Use your library account to continue.</p>

          <div className="field"><label htmlFor="email">Email</label><input id="email" type="email" name="email" value={form.email} onChange={handleChange} required autoFocus /></div>
          <div className="field"><label htmlFor="password">Password</label><input id="password" type="password" name="password" value={form.password} onChange={handleChange} required /></div>
          {error && <p className="error-text">{error}</p>}
          <button type="submit" className="btn-primary" style={{ width: "100%", marginTop: "8px" }} disabled={submitting}>{submitting ? "Signing in..." : "Sign in"}</button>
          <p style={{ marginTop: "22px", fontSize: "14px", color: "var(--ink-soft)" }}>New here? <Link to="/register">Create an account</Link></p>
        </form>
      </div>
    </div>
  );
}

export default Login;
