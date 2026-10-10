import { useState } from "react";
import { NavLink, Outlet } from "react-router-dom";
import { useAuth } from "../AuthContext";

const adminLinks = [
  { to: "/app/books", label: "Catalog", icon: "▦" },
  { to: "/app/borrow-records", label: "Borrow records", icon: "↔" },
  { to: "/app/students", label: "Members", icon: "◎" },
  { to: "/app/login-attempts", label: "Login activity", icon: "◌" },
];

const studentLinks = [
  { to: "/app", label: "Browse catalog", icon: "⌕" },
  { to: "/app?view=my-books", label: "My books", icon: "▤" },
];

function Shell() {
  const { user, logout } = useAuth();
  const [menuOpen, setMenuOpen] = useState(false);
  const isAdmin = user?.role === "ADMIN";
  const links = isAdmin ? adminLinks : studentLinks;

  return (
    <div className="app-layout">
      <div className={`sidebar-backdrop ${menuOpen ? "is-open" : ""}`} onClick={() => setMenuOpen(false)} />
      <aside className={`app-sidebar ${menuOpen ? "is-open" : ""}`}>
        <div className="brand-block">
          <div className="brand-logo" aria-label="Smart Library logo">
            <svg viewBox="0 0 48 48" role="img" aria-hidden="true">
              <path d="M8 10.5c6-2 11.3-.8 16 3.1v25.7c-4.7-3.9-10-5.1-16-3.1V10.5Z" fill="#F1E7D8" />
              <path d="M40 10.5c-6-2-11.3-.8-16 3.1v25.7c4.7-3.9 10-5.1 16-3.1V10.5Z" fill="#E4D5BE" />
              <path d="M24 13.6v25.7" stroke="#B98B4E" strokeWidth="2" />
              <path d="M31.5 7.5h5.8v13l-2.9-2.2-2.9 2.2v-13Z" fill="#B98B4E" />
            </svg>
          </div>
          <div><div className="brand-name">Smart Library</div><div className="brand-subtitle">{isAdmin ? "Admin workspace" : "Your reading space"}</div></div>
        </div>
        <div className="sidebar-section-label">Workspace</div>
        <nav className="sidebar-nav">
          {links.map((link) => <NavLink key={link.to} to={link.to} end={link.to === "/app"} onClick={() => setMenuOpen(false)} className={({ isActive }) => `sidebar-link ${isActive ? "active" : ""}`}><span className="nav-icon">{link.icon}</span><span>{link.label}</span></NavLink>)}
        </nav>
        <div className="sidebar-footer"><span className="status-dot" /> All systems operational</div>
      </aside>

      <div className="app-content">
        <header className="topbar">
          <button className="menu-toggle" onClick={() => setMenuOpen(true)} aria-label="Open navigation menu">☰</button>
          <div className="topbar-title">{isAdmin ? "Admin workspace" : "Member dashboard"}</div>
          <div className="user-area">
            <div className="avatar">{(user?.name || "U").charAt(0).toUpperCase()}</div>
            <div className="user-copy"><strong>{user?.name}</strong><span>{isAdmin ? "Administrator" : "Library member"}</span></div>
            <span className={isAdmin ? "badge badge-admin" : "badge badge-student"}>{user?.role}</span>
            <button className="btn-ghost btn-sm" onClick={logout}>Log out</button>
          </div>
        </header>
        <main className="app-main"><Outlet /></main>
      </div>
    </div>
  );
}

export default Shell;
