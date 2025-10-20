import { useEffect } from "react";
import { useNavigate } from "react-router";

import { loginUrl } from "../lib/discord";
import Layout from "../components/Layout";
import { useAuth } from "../hooks/useAuth";

export default function Login() {
  const { isAuthenticated } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    if (isAuthenticated) {
      navigate("/dashboard");
    }
  }, [isAuthenticated, navigate]);

  return (
    <Layout showHeader={false}>
      <div className="flex flex-col items-center justify-center min-h-[80vh]">
        <div className="text-center mb-10">
          <h1 className="text-4xl font-bold text-white mb-3">My Notes</h1>
          <p className="text-gray-400 max-w-md">
            A simple and secure way to store and manage your personal notes.
            Sign in with Discord to get started.
          </p>
        </div>

        <div className="bg-gray-800 p-8 rounded-lg shadow-lg border border-gray-700 w-full max-w-md">
          <h2 className="text-2xl font-semibold text-white mb-6 text-center">
            Welcome!
          </h2>

          <div className="flex justify-center">
            <a
              href={loginUrl}
              className="flex items-center justify-center gap-2 bg-[#5865F2] hover:bg-[#4752C4] text-white font-medium px-6 py-3 rounded-md transition-colors w-full"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                width="24"
                height="24"
                viewBox="0 0 24 24"
                fill="currentColor"
              >
                <path d="M20.317 4.492c-1.53-.69-3.17-1.2-4.885-1.49a.075.075 0 0 0-.079.036c-.21.385-.392.86-.534 1.246a18.566 18.566 0 0 0-5.635 0A9.746 9.746 0 0 0 8.65 3.039a.077.077 0 0 0-.079-.037 19.414 19.414 0 0 0-4.885 1.49.07.07 0 0 0-.032.028C.533 9.091-.32 13.555.099 17.961a.08.08 0 0 0 .031.055 19.618 19.618 0 0 0 5.91 2.98.075.075 0 0 0 .082-.026c.458-.624.865-1.289 1.217-1.989a.075.075 0 0 0-.041-.105 13.949 13.949 0 0 1-1.825-.873.075.075 0 0 1-.008-.125c.123-.093.245-.19.362-.287a.075.075 0 0 1 .078-.01c3.927 1.793 8.18 1.793 12.061 0a.075.075 0 0 1 .078.01c.118.098.24.195.363.288a.075.075 0 0 1-.006.125c-.585.348-1.192.642-1.828.875a.075.075 0 0 0-.041.105c.36.698.772 1.362 1.217 1.988a.075.075 0 0 0 .082.026c1.961-.61 3.906-1.519 5.915-2.98a.076.076 0 0 0 .032-.054c.5-5.177-.838-9.674-3.549-13.66a.061.061 0 0 0-.031-.03zM8.02 15.278c-1.182 0-2.157-1.085-2.157-2.416 0-1.331.956-2.416 2.157-2.416 1.21 0 2.176 1.095 2.157 2.416 0 1.331-.956 2.416-2.157 2.416zm7.975 0c-1.183 0-2.157-1.085-2.157-2.416 0-1.331.955-2.416 2.157-2.416 1.21 0 2.176 1.095 2.157 2.416 0 1.331-.946 2.416-2.157 2.416z" />
              </svg>
              Sign in with Discord
            </a>
          </div>

          <div className="mt-6 text-center text-gray-500 text-sm">
            By signing in, you agree to our Terms of Service and Privacy Policy.
          </div>
        </div>
      </div>
    </Layout>
  );
}
