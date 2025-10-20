import { Loader2 } from "lucide-react";

interface SpinnerProps {
  size?: "small" | "medium" | "large";
  color?: "primary" | "white";
  className?: string;
}

export default function Spinner({
  size = "medium",
  color = "primary",
  className = "",
}: SpinnerProps) {
  const sizeClasses = {
    small: "w-4 h-4",
    medium: "w-6 h-6",
    large: "w-8 h-8",
  };

  const colorClasses = {
    primary: "text-blue-500",
    white: "text-white",
  };

  return (
    <div role="status" className={className}>
      <Loader2
        className={`animate-spin ${sizeClasses[size]} ${colorClasses[color]}`}
      />
      <span className="sr-only">Loading...</span>
    </div>
  );
}
