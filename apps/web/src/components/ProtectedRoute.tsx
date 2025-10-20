import { useNavigate, useLocation, Outlet } from "react-router";
import { useAuth } from "../hooks/useAuth";
import Spinner from "./Spinner";

export default function ProtectedRoute() {
  const { isAuthenticated, isLoading } = useAuth();
  const location = useLocation();
  const navigate = useNavigate();

  if (isLoading) {
    return (
      <div className="flex justify-center items-center min-h-screen">
        <Spinner size="large" />
      </div>
    );
  }

  if (!isAuthenticated) {
    navigate("/login", { state: { from: location }, replace: true });
  }

  return <Outlet />;
}
