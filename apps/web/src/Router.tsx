import { createBrowserRouter } from "react-router";

import Dashboard from "./pages/Dashboard";
import NoteDetail from "./pages/NoteDetail";
import Login from "./pages/Login";
import AuthCallback from "./pages/AuthCallback";
import ProtectedRoute from "./components/ProtectedRoute";
import PublicRoute from "./components/PublicRoute";

export const router = createBrowserRouter([
  {
    path: "/",
    element: <ProtectedRoute />,
    children: [
      { index: true, element: <Dashboard /> },
      { path: "notes/:id", element: <NoteDetail /> },
    ],
  },
  {
    path: "/",
    element: <PublicRoute />,
    children: [
      { path: "login", element: <Login /> },
      { path: "auth/callback", element: <AuthCallback /> },
    ],
  },
  { path: "*", element: <Dashboard /> },
]);
