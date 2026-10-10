import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import { AuthProvider, useAuth } from "./AuthContext";
import ProtectedRoute from "./components/ProtectedRoute";
import Shell from "./components/Shell";
import Login from "./pages/Login";
import Register from "./pages/Register";
import StudentHome from "./pages/StudentHome";
import AdminBooks from "./pages/AdminBooks";
import AdminBorrowRecords from "./pages/AdminBorrowRecords";
import AdminStudents from "./pages/AdminStudents";
import AdminLoginAttempts from "./pages/AdminLoginAttempts";

// Sends a logged-in user to the right landing page for their role, so /app
// is a single stable link to put in a bookmark or a nav brand-click.
function AppIndex() {
  const { user } = useAuth();
  if (user?.role === "ADMIN") return <Navigate to="/app/books" replace />;
  return <StudentHome />;
}

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          <Route path="/" element={<Login />} />
          <Route path="/register" element={<Register />} />

          <Route
            path="/app"
            element={
              <ProtectedRoute>
                <Shell />
              </ProtectedRoute>
            }
          >
            <Route index element={<AppIndex />} />
            <Route
              path="books"
              element={
                <ProtectedRoute requireRole="ADMIN">
                  <AdminBooks />
                </ProtectedRoute>
              }
            />
            <Route
              path="borrow-records"
              element={
                <ProtectedRoute requireRole="ADMIN">
                  <AdminBorrowRecords />
                </ProtectedRoute>
              }
            />
            <Route
              path="students"
              element={
                <ProtectedRoute requireRole="ADMIN">
                  <AdminStudents />
                </ProtectedRoute>
              }
            />
            <Route
              path="login-attempts"
              element={
                <ProtectedRoute requireRole="ADMIN">
                  <AdminLoginAttempts />
                </ProtectedRoute>
              }
            />
          </Route>

          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;
