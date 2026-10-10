import { useEffect, useState } from "react";
import api from "../api";

const emptyForm = { title: "", author: "", isbn: "", category: "", totalCopies: 1 };

function AdminBooks() {
  const [books, setBooks] = useState([]);
  const [form, setForm] = useState(emptyForm);
  const [editingId, setEditingId] = useState(null);
  const [error, setError] = useState("");

  async function load() {
    const res = await api.get("/books");
    setBooks(res.data);
  }

  useEffect(() => { load(); }, []);

  function handleChange(e) {
    const { name, value } = e.target;
    setForm({ ...form, [name]: name === "totalCopies" ? Number(value) : value });
  }

  function startEdit(book) {
    setEditingId(book.id);
    setForm({ title: book.title, author: book.author, isbn: book.isbn, category: book.category || "", totalCopies: book.totalCopies });
  }

  function cancelEdit() {
    setEditingId(null);
    setForm(emptyForm);
  }

  async function submit(e) {
    e.preventDefault();
    setError("");
    try {
      if (editingId) {
        await api.put(`/books/${editingId}`, form);
      } else {
        await api.post("/books", form);
      }
      cancelEdit();
      load();
    } catch (err) {
      setError(err.response?.data?.error || "Could not save this book");
    }
  }

  async function remove(id) {
    if (!window.confirm("Remove this book from the catalog?")) return;
    await api.delete(`/books/${id}`);
    load();
  }

  return (
    <div>
      <h1 style={{ fontSize: "24px", marginBottom: "20px" }}>Catalog</h1>

      <div style={{ display: "grid", gridTemplateColumns: "320px 1fr", gap: "24px", alignItems: "start" }}>
        <form onSubmit={submit} className="panel">
          <h3 style={{ fontSize: "16px", marginBottom: "16px" }}>{editingId ? "Edit book" : "Add a book"}</h3>

          <div className="field">
            <label>Title</label>
            <input name="title" value={form.title} onChange={handleChange} required />
          </div>
          <div className="field">
            <label>Author</label>
            <input name="author" value={form.author} onChange={handleChange} required />
          </div>
          <div className="field">
            <label>ISBN</label>
            <input name="isbn" value={form.isbn} onChange={handleChange} required />
          </div>
          <div className="field">
            <label>Category</label>
            <input name="category" value={form.category} onChange={handleChange} />
          </div>
          <div className="field">
            <label>Total copies</label>
            <input type="number" min="1" name="totalCopies" value={form.totalCopies} onChange={handleChange} required />
          </div>

          {error && <p className="error-text">{error}</p>}

          <div style={{ display: "flex", gap: "10px" }}>
            <button type="submit" className="btn-primary">{editingId ? "Save changes" : "Add book"}</button>
            {editingId && <button type="button" className="btn-ghost" onClick={cancelEdit}>Cancel</button>}
          </div>
        </form>

        <div className="card table-wrap" style={{ padding: 0 }}>
          <table>
            <thead>
              <tr><th>Title</th><th>Author</th><th>ISBN</th><th>Copies</th><th></th></tr>
            </thead>
            <tbody>
              {books.map((b) => (
                <tr key={b.id}>
                  <td style={{ fontWeight: 600 }}>{b.title}</td>
                  <td>{b.author}</td>
                  <td>{b.isbn}</td>
                  <td>{b.availableCopies} / {b.totalCopies}</td>
                  <td className="action-cell">
                    <div className="action-buttons">
                      <button className="btn-ghost btn-sm" onClick={() => startEdit(b)}>Edit</button>
                      <button className="btn-danger btn-sm" onClick={() => remove(b.id)}>Delete</button>
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

export default AdminBooks;
