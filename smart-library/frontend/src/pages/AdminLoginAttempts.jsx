import { useEffect, useState } from "react";
import api from "../api";

function AdminLoginAttempts() {
  const [attempts, setAttempts] = useState([]);
  const [flaggedOnly, setFlaggedOnly] = useState(false);

  async function load() {
    const res = await api.get(flaggedOnly ? "/login-attempts/flagged" : "/login-attempts");
    setAttempts(res.data);
  }

  useEffect(() => { load(); }, [flaggedOnly]);

  return (
    <div>
      <h1 style={{ fontSize: "24px", marginBottom: "4px" }}>Login Activity</h1>
      <p className="help-text" style={{ marginBottom: "20px" }}>
        Every login attempt is logged. Repeated failures auto-lock an account -
        unlock it from the Students page.
      </p>

      <label style={{ display: "flex", alignItems: "center", gap: "8px", marginBottom: "16px", fontWeight: 500 }}>
        <input type="checkbox" style={{ width: "auto" }} checked={flaggedOnly} onChange={(e) => setFlaggedOnly(e.target.checked)} />
        Show flagged only
      </label>

      {attempts.length === 0 ? (
        <div className="empty-state">No login activity to show.</div>
      ) : (
        <div className="card table-wrap" style={{ padding: 0 }}>
          <table>
            <thead>
              <tr><th>Time</th><th>Email</th><th>IP Address</th><th>Status</th><th>Flagged</th><th>Reason</th></tr>
            </thead>
            <tbody>
              {attempts.map((a) => (
                <tr key={a.id} style={a.flagged ? { background: "var(--rust-bg)" } : undefined}>
                  <td>{new Date(a.attemptedAt).toLocaleString()}</td>
                  <td>{a.email}</td>
                  <td>{a.ipAddress}</td>
                  <td>
                    <span className={a.success ? "badge badge-ok" : "badge badge-warn"}>
                      {a.success ? "Success" : "Failed"}
                    </span>
                  </td>
                  <td>{a.flagged ? "Yes" : "No"}</td>
                  <td className="help-text">{a.reason || "-"}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}

export default AdminLoginAttempts;
