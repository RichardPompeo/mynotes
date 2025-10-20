import { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router";
import { useAuth } from "../hooks/useAuth";
import { authApi } from "../services/api";
import Layout from "../components/Layout";
import Spinner from "../components/Spinner";
import type { AxiosError } from "axios";

export default function AuthCallback() {
  const navigate = useNavigate();
  const { login } = useAuth();
  const didExchange = useRef(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const handleAuth = async () => {
      if (didExchange.current) return;

      didExchange.current = true;

      try {
        const params = new URLSearchParams(window.location.search);
        const code = params.get("code");
        const error = params.get("error");

        if (error) {
          setError("Authentication failed. Please try again.");
          return;
        }

        if (!code) {
          setError("No authorization code found.");
          return;
        }

        const response = await authApi.exchangeCode(code);
        const { token, user, provider_token } = response.data;

        if (provider_token)
          localStorage.setItem("provider_token", provider_token);

        if (!token || !user) {
          const serverMsg = response.data?.error || response.data?.details;

          setError(
            serverMsg
              ? String(serverMsg)
              : "Failed to authenticate with Discord.",
          );
          return;
        }

        login(token, user);
        navigate("/dashboard");
      } catch (err: unknown) {
        console.error("Authentication error:", err);

        const axiosErr = err as unknown as AxiosError<{
          details?: string;
          error?: string;
        }>;

        const serverDetails =
          axiosErr?.response?.data?.details || axiosErr?.response?.data?.error;

        setError(
          serverDetails
            ? String(serverDetails)
            : "An error occurred during authentication. Please try again.",
        );
      }
    };

    handleAuth();
  }, [navigate, login]);

  return (
    <Layout showHeader={false}>
      <div className="flex flex-col items-center justify-center min-h-[80vh]">
        {error ? (
          <div className="bg-red-900/20 border border-red-500 rounded-lg p-6 max-w-md">
            <h2 className="text-xl font-semibold text-white mb-2">
              Authentication Error
            </h2>
            <p className="text-red-300 mb-4">{error}</p>
            <button
              onClick={() => navigate("/login")}
              className="bg-gray-700 hover:bg-gray-600 text-white px-4 py-2 rounded transition-colors"
            >
              Return to Login
            </button>
          </div>
        ) : (
          <div className="flex flex-col items-center text-center">
            <Spinner size="large" color="primary" className="mb-4" />
            <h2 className="text-xl font-semibold text-white mb-2">
              Authenticating with Discord...
            </h2>
            <p className="text-gray-400">
              Please wait while we complete your authentication.
            </p>
          </div>
        )}
      </div>
    </Layout>
  );
}
