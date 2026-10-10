import { useEffect, useState } from "react";
import api from "../api";

function AdminBorrowRecords() {
  const [records, setRecords] = useState([]);

  async function load() {
    const res = await api.get("/borrow/all");
    setRecords(res.data);
  }

  useEffect(() => { load(); }, []);

  async function forceReturn(id) {
    await api.post(`/borrow/${id}/return`);
    load();
  }

  return (
    <div>
      <h1 style={{ fontSize: "24px", marginBottom: "20px" }}>Borrow Records</h1>

      {records.length === 0 ? (
        <div className="empty-state">No borrow activity yet.</div>
      ) : (
        <div className="card table-wrap" style={{ padding: 0 }}>
          <table>
            <thead>
              <tr><th>Book</th><th>Student</th><th>Borrowed</th><th>Due</th><th>Status</th><th></th></tr>
            </thead>
            <tbody>
              {records.map((r) => (
                <tr key={r.id}>
                  <td style={{ fontWeight: 600 }}>{r.book?.title}</td>
                  <td>{r.student?.name} <span className="help-text">({r.student?.email})</span></td>
                  <td>{new Date(r.borrowedAt).toLocaleDateString()}</td>
                  <td>{new Date(r.dueDate).toLocaleDateString()}</td>
                  <td>
                    <span className={r.returned ? "badge badge-ok" : "badge badge-warn"}>
                      {r.returned ? "Returned" : "Out"}
                    </span>
                  </td>
                  <td>
                    {!r.returned && (
                      <button className="btn-ghost btn-sm" onClick={() => forceReturn(r.id)}>Mark returned</button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}

export default AdminBorrowRecords;
