import { useEffect, useState } from "react";
import { useSearchParams } from "react-router-dom";
import api from "../api";

function StudentHome() {
  const [searchParams, setSearchParams] = useSearchParams();
  const [tab, setTab] = useState(searchParams.get("view") === "my-books" ? "my-books" : "browse");
  const [books, setBooks] = useState([]);
  const [myRecords, setMyRecords] = useState([]);
  const [query, setQuery] = useState("");
  const [message, setMessage] = useState("");

  async function loadBooks(q) {
    const res = await api.get("/books", { params: q ? { q } : {} });
    setBooks(res.data);
  }

  async function loadMyRecords() {
    const res = await api.get("/borrow/my");
    setMyRecords(res.data);
  }

  useEffect(() => {
    loadBooks();
    loadMyRecords();
  }, []);

  async function borrow(bookId) {
    setMessage("");
    try {
      await api.post(`/borrow/${bookId}`);
      setMessage("Borrowed. Check \"My Books\" for the due date.");
      loadBooks(query);
      loadMyRecords();
    } catch (err) {
      setMessage(err.response?.data?.error || "Could not borrow this book");
    }
  }

  async function returnBook(recordId) {
    setMessage("");
    try {
      await api.post(`/borrow/${recordId}/return`);
      setMessage("Returned. Thanks!");
      loadBooks(query);
      loadMyRecords();
    } catch (err) {
      setMessage(err.response?.data?.error || "Could not return this book");
    }
  }

  const activeCount = myRecords.filter((r) => !r.returned).length;

  function changeTab(nextTab) {
    setTab(nextTab);
    setSearchParams(nextTab === "browse" ? {} : { view: nextTab });
  }

  return (
    <div>
      <h1 style={{ fontSize: "26px", marginBottom: "4px" }}>Welcome back</h1>
      <p className="help-text" style={{ marginBottom: "24px" }}>
        {activeCount > 0 ? `You have ${activeCount} book${activeCount > 1 ? "s" : ""} out right now.` : "You have no books out right now."}
      </p>

      <div style={{ display: "flex", gap: "8px", marginBottom: "20px", borderBottom: "1px solid var(--line)" }}>
        {["browse", "my-books"].map((t) => (
          <button
            key={t}
            onClick={() => changeTab(t)}
            style={{
              background: "transparent",
              color: tab === t ? "var(--espresso)" : "var(--ink-soft)",
              borderBottom: tab === t ? "2px solid var(--brass)" : "2px solid transparent",
              borderRadius: 0,
              padding: "10px 4px",
              marginRight: "20px",
              fontSize: "14px",
            }}
          >
            {t === "browse" ? "Browse Catalog" : "My Books"}
          </button>
        ))}
      </div>

      {message && <p className="help-text" style={{ marginBottom: "16px" }}>{message}</p>}

      {tab === "browse" && (
        <>
          <div style={{ display: "flex", gap: "10px", marginBottom: "18px", maxWidth: "420px" }}>
            <input
              placeholder="Search by title or author"
              value={query}
              onChange={(e) => setQuery(e.target.value)}
              onKeyDown={(e) => e.key === "Enter" && loadBooks(query)}
            />
            <button className="btn-ghost" onClick={() => loadBooks(query)}>Search</button>
          </div>

          {books.length === 0 ? (
            <div className="empty-state">No books match your search.</div>
          ) : (
            <div className="card table-wrap" style={{ padding: 0 }}>
              <table>
                <thead>
                  <tr><th>Title</th><th>Author</th><th>Category</th><th>Available</th><th></th></tr>
                </thead>
                <tbody>
                  {books.map((b) => (
                    <tr key={b.id}>
                      <td style={{ fontWeight: 600 }}>{b.title}</td>
                      <td>{b.author}</td>
                      <td>{b.category || "-"}</td>
                      <td>{b.availableCopies} / {b.totalCopies}</td>
                      <td>
                        <button
                          className="btn-primary btn-sm"
                          disabled={b.availableCopies <= 0}
                          onClick={() => borrow(b.id)}
                        >
                          {b.availableCopies > 0 ? "Borrow" : "Unavailable"}
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </>
      )}

      {tab === "my-books" && (
        myRecords.length === 0 ? (
          <div className="empty-state">You haven't borrowed anything yet.</div>
        ) : (
          <div className="card table-wrap" style={{ padding: 0 }}>
            <table>
              <thead>
                <tr><th>Title</th><th>Borrowed</th><th>Due</th><th>Status</th><th></th></tr>
              </thead>
              <tbody>
                {myRecords.map((r) => (
                  <tr key={r.id}>
                    <td style={{ fontWeight: 600 }}>{r.book?.title}</td>
                    <td>{new Date(r.borrowedAt).toLocaleDateString()}</td>
                    <td>{new Date(r.dueDate).toLocaleDateString()}</td>
                    <td>
                      <span className={r.returned ? "badge badge-ok" : "badge badge-warn"}>
                        {r.returned ? "Returned" : "Out"}
                      </span>
                    </td>
                    <td>
                      {!r.returned && (
                        <button className="btn-ghost btn-sm" onClick={() => returnBook(r.id)}>Return</button>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )
      )}
    </div>
  );
}

export default StudentHome;
