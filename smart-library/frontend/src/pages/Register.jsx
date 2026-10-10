import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import api from "../api";

function Register() {
  const navigate = useNavigate();
  const [form, setForm] = useState({ name: "", email: "", password: "" });
  const [errors, setErrors] = useState({});
  const [submitting, setSubmitting] = useState(false);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  async function submit(e) {
    e.preventDefault();
    setErrors({});
    setSubmitting(true);
    try {
      await api.post("/auth/register", form);
      navigate("/", { state: { registered: true } });
    } catch (err) {
      if (err.response?.status === 400 && typeof err.response.data === "object") {
        setErrors(err.response.data);
      } else {
        setErrors({ general: "Something went wrong. Please try again." });
      }
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div style={{
      minHeight: "100vh",
      display: "flex",
      alignItems: "center",
      justifyContent: "center",
      background: "var(--paper)",
      padding: "40px",
    }}>
      <form onSubmit={submit} className="card" style={{ width: "100%", maxWidth: "400px" }}>
        <h2 style={{ fontSize: "24px", marginBottom: "6px" }}>Create your account</h2>
        <p className="help-text" style={{ marginBottom: "24px" }}>
          New accounts start as students. Staff access is granted separately by a librarian.
        </p>

        <div className="field">
          <label htmlFor="name">Full name</label>
          <input id="name" name="name" value={form.name} onChange={handleChange} required />
          {errors.name && <p className="error-text">{errors.name}</p>}
        </div>
        <div className="field">
          <label htmlFor="email">Email</label>
          <input id="email" type="email" name="email" value={form.email} onChange={handleChange} required />
          {errors.email && <p className="error-text">{errors.email}</p>}
        </div>
        <div className="field">
          <label htmlFor="password">Password</label>
          <input id="password" type="password" name="password" value={form.password} onChange={handleChange} required minLength={8} />
          <p className="help-text">At least 8 characters.</p>
          {errors.password && <p className="error-text">{errors.password}</p>}
        </div>

        {errors.error && <p className="error-text">{errors.error}</p>}
        {errors.general && <p className="error-text">{errors.general}</p>}

        <button type="submit" className="btn-primary" style={{ width: "100%", marginTop: "8px" }} disabled={submitting}>
          {submitting ? "Creating account..." : "Create account"}
        </button>

        <p style={{ marginTop: "22px", fontSize: "14px", color: "var(--ink-soft)" }}>
          Already have an account? <Link to="/">Sign in</Link>
        </p>
      </form>
    </div>
  );
}

export default Register;
