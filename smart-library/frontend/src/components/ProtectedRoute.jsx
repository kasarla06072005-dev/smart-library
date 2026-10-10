import { Navigate } from "react-router-dom";
import { useAuth } from "../AuthContext";

// requireRole: if set, only that role may pass - anything else (including the
// other valid role) gets redirected, not just unauthenticated users. This is
// what keeps a logged-in student from reaching admin-only screens by typing
// the URL directly - the UI enforces it here, and the API enforces it again
// server-side (SecurityConfig), so hiding a nav link is never the only guard.
function ProtectedRoute({ children, requireRole }) {
  const { user, loading } = useAuth();

  if (loading) {
    return <div className="empty-state">Loading...</div>;
  }
  if (!user) {
    return <Navigate to="/" replace />;
  }
  if (requireRole && user.role !== requireRole) {
    return <Navigate to="/app" replace />;
  }
  return children;
}

export default ProtectedRoute;
