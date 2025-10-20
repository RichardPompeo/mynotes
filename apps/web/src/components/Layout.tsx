import { type ReactNode } from "react";
import { useNavigate } from "react-router";
import { LogOut } from "lucide-react";

import { useAuth } from "../hooks/useAuth";

interface LayoutProps {
  children: ReactNode;
  title?: string;
  showHeader?: boolean;
}

export default function Layout({
  children,
  title = "My Notes",
  showHeader = true,
}: LayoutProps) {
  const { isAuthenticated, user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <div className="min-h-screen bg-gray-900 text-gray-100 flex flex-col">
      {showHeader && (
        <header className="bg-gray-800 border-b border-gray-700 px-4 py-3 shadow-md">
          <div className="container mx-auto flex items-center justify-between">
            <div className="flex items-center space-x-2">
              <h1 className="text-xl font-bold text-white">{title}</h1>
            </div>
            {isAuthenticated && (
              <div className="flex items-center space-x-4">
                <div className="flex items-center space-x-2">
                  <div className="text-sm text-gray-300">
                    {user?.global_name || user?.username}
                  </div>
                  {user?.avatar && (
                    <img
                      src={`https://cdn.discordapp.com/avatars/${user.id}/${user.avatar}.png`}
                      alt="Avatar"
                      className="h-8 w-8 rounded-full"
                    />
                  )}
                </div>
                <button
                  onClick={handleLogout}
                  className="text-sm text-gray-400 hover:text-white transition-colors flex items-center gap-1"
                >
                  <LogOut size={16} />
                  Logout
                </button>
              </div>
            )}
          </div>
        </header>
      )}
      <main className="flex-1 p-4">
        <div className="container mx-auto">{children}</div>
      </main>
      <footer className="bg-gray-800 border-t border-gray-700 py-3 text-center text-gray-400 text-sm">
        <div className="container mx-auto">
          My Notes App &copy; {new Date().getFullYear()}
        </div>
      </footer>
    </div>
  );
}
