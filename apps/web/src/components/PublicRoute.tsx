import { useNavigate, Outlet } from "react-router";

import { useAuth } from "../hooks/useAuth";
import Spinner from "./Spinner";

export default function PublicRoute() {
  const { isAuthenticated, isLoading } = useAuth();
  const navigate = useNavigate();

  if (isLoading) {
    return (
      <div className="flex justify-center items-center min-h-screen">
        <Spinner size="large" />
      </div>
    );
  }

  if (isAuthenticated) {
    navigate("/", { replace: true });
  }

  return <Outlet />;
}
