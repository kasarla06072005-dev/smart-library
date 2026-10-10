import { useEffect, useState } from "react";
import api from "../api";

const emptyForm = { name: "", email: "", password: "", role: "STUDENT" };

function AdminStudents() {
  const [students, setStudents] = useState([]);
  const [form, setForm] = useState(emptyForm);
  const [editingId, setEditingId] = useState(null);
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");
  const [submitting, setSubmitting] = useState(false);

  async function load() {
    try {
      const res = await api.get("/students");
      setStudents(res.data);
    } catch (err) {
      setError(err.response?.data?.error || "Failed to load students.");
    }
  }

  useEffect(() => { load(); }, []);

  function handleChange(e) {
    const { name, value } = e.target;
    setForm({ ...form, [name]: value });
  }

  function startEdit(student) {
    setEditingId(student.id);
    setForm({
      name: student.name,
      email: student.email,
      password: "",
      role: student.role || "STUDENT",
    });
    setError("");
    setMessage("");
  }

  function cancelEdit() {
    setEditingId(null);
    setForm(emptyForm);
    setError("");
  }

  async function submit(e) {
    e.preventDefault();
    setError("");
    setMessage("");
    setSubmitting(true);
    try {
      if (editingId) {
        await api.put(`/students/${editingId}`, form);
        setMessage("Student details updated successfully.");
      } else {
        await api.post("/students", form);
        setMessage("Student user added successfully.");
      }
      cancelEdit();
      load();
    } catch (err) {
      if (err.response?.status === 400 && typeof err.response.data === "object" && !err.response.data.error) {
        const firstErr = Object.values(err.response.data)[0];
        setError(firstErr || "Validation error");
      } else {
        setError(err.response?.data?.error || "Could not save student");
      }
    } finally {
      setSubmitting(false);
    }
  }

  async function remove(id, name) {
    if (!window.confirm(`Are you sure you want to delete student "${name}"?`)) return;
    setError("");
    setMessage("");
    try {
      await api.delete(`/students/${id}`);
      setMessage(`Student "${name}" deleted successfully.`);
      if (editingId === id) {
        cancelEdit();
      }
      load();
    } catch (err) {
      setError(err.response?.data?.error || "Could not delete student.");
    }
  }

  async function unlock(id) {
    setMessage("");
    setError("");
    try {
      await api.post(`/students/${id}/unlock`);
      setMessage("Account unlocked.");
      load();
    } catch (err) {
      setError(err.response?.data?.error || "Failed to unlock account.");
    }
  }

  return (
    <div>
      <h1 style={{ fontSize: "24px", marginBottom: "20px" }}>Students & Users</h1>
      {message && <p className="help-text" style={{ color: "var(--sage)", fontWeight: 600, marginBottom: "16px" }}>{message}</p>}
      {error && <p className="error-text" style={{ marginBottom: "16px" }}>{error}</p>}

      <div style={{ display: "grid", gridTemplateColumns: "320px 1fr", gap: "24px", alignItems: "start" }}>
        <form onSubmit={submit} className="panel">
          <h3 style={{ fontSize: "16px", marginBottom: "16px" }}>
            {editingId ? "Edit user" : "Add a student / user"}
          </h3>

          <div className="field">
            <label htmlFor="student-name">Full name</label>
            <input
              id="student-name"
              name="name"
              value={form.name}
              onChange={handleChange}
              placeholder="e.g. Alex Johnson"
              required
            />
          </div>

          <div className="field">
            <label htmlFor="student-email">Email</label>
            <input
              id="student-email"
              type="email"
              name="email"
              value={form.email}
              onChange={handleChange}
              placeholder="e.g. alex@student.edu"
              required
            />
          </div>

          <div className="field">
            <label htmlFor="student-password">
              Password {editingId && <span style={{ fontWeight: 400, fontSize: "12px" }}>(leave blank to keep unchanged)</span>}
            </label>
            <input
              id="student-password"
              type="password"
              name="password"
              value={form.password}
              onChange={handleChange}
              placeholder={editingId ? "••••••••" : "Min 8 characters"}
              required={!editingId}
              minLength={form.password ? 8 : undefined}
            />
          </div>

          <div className="field">
            <label htmlFor="student-role">Role</label>
            <select
              id="student-role"
              name="role"
              value={form.role}
              onChange={handleChange}
            >
              <option value="STUDENT">STUDENT</option>
              <option value="ADMIN">ADMIN</option>
            </select>
          </div>

          <div style={{ display: "flex", gap: "10px", marginTop: "20px" }}>
            <button type="submit" className="btn-primary" disabled={submitting}>
              {submitting ? "Saving..." : (editingId ? "Save changes" : "Add student")}
            </button>
            {editingId && (
              <button type="button" className="btn-ghost" onClick={cancelEdit}>
                Cancel
              </button>
            )}
          </div>
        </form>

        <div className="card table-wrap" style={{ padding: 0 }}>
          <table>
            <thead>
              <tr>
                <th>Name</th>
                <th>Email</th>
                <th>Role</th>
                <th>Status</th>
                <th>Joined</th>
                <th style={{ textAlign: "right" }}>Actions</th>
              </tr>
            </thead>
            <tbody>
              {students.map((s) => (
                <tr key={s.id}>
                  <td style={{ fontWeight: 600 }}>{s.name}</td>
                  <td>{s.email}</td>
                  <td>
                    <span className={s.role === "ADMIN" ? "badge badge-admin" : "badge badge-student"}>
                      {s.role}
                    </span>
                  </td>
                  <td>
                    <span className={s.locked ? "badge badge-warn" : "badge badge-ok"}>
                      {s.locked ? "Locked" : "Active"}
                    </span>
                  </td>
                  <td>{new Date(s.createdAt).toLocaleDateString()}</td>
                  <td className="action-cell">
                    <div className="action-buttons action-buttons-end">
                    {s.locked && (
                      <button className="btn-ghost btn-sm" onClick={() => unlock(s.id)}>
                        Unlock
                      </button>
                    )}
                    <button className="btn-ghost btn-sm" onClick={() => startEdit(s)}>
                      Edit
                    </button>
                    <button className="btn-danger btn-sm" onClick={() => remove(s.id, s.name)}>
                      Delete
                    </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}

export default AdminStudents;
