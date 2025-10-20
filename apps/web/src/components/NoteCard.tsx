import { useState } from "react";

import { type Note } from "../types/Note";
import { Edit, Trash2 } from "lucide-react";

interface NoteCardProps {
  note: Note;
  onEdit: (note: Note) => void;
  onDelete: (noteId: string) => void;
}

export default function NoteCard({ note, onEdit, onDelete }: NoteCardProps) {
  const [isExpanded, setIsExpanded] = useState(false);

  const toggleExpand = () => setIsExpanded(!isExpanded);

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString(undefined, {
      year: "numeric",
      month: "short",
      day: "numeric",
    });
  };

  return (
    <div className="bg-gray-800 rounded-lg shadow-md overflow-hidden border border-gray-700 hover:border-gray-600 transition-all">
      <div className="p-4">
        <div className="flex justify-between items-start">
          <h3 className="text-lg font-semibold text-white mb-2">
            {note.title}
          </h3>
          <div className="flex space-x-2">
            <button
              onClick={() => onEdit(note)}
              className="text-gray-400 hover:text-blue-400 transition-colors"
              aria-label="Edit note"
            >
              <Edit size={18} />
            </button>
            <button
              onClick={() => onDelete(note.id)}
              className="text-gray-400 hover:text-red-400 transition-colors"
              aria-label="Delete note"
            >
              <Trash2 size={18} />
            </button>
          </div>
        </div>
        <div className={`mt-2 ${isExpanded ? "" : "line-clamp-3"}`}>
          {note.content.split("\n").map((paragraph, index) => (
            <p key={index} className="text-gray-300 mb-2">
              {paragraph}
            </p>
          ))}
        </div>
        {note.content.split("\n").length > 3 && (
          <button
            onClick={toggleExpand}
            className="text-blue-400 hover:text-blue-300 text-sm mt-1 transition-colors"
          >
            {isExpanded ? "Show less" : "Read more"}
          </button>
        )}
        <div className="flex justify-between items-center mt-4 text-xs text-gray-500">
          <div>Created: {formatDate(note.createdAt)}</div>
          <div>
            {note.updatedAt !== note.createdAt
              ? `Updated: ${formatDate(note.updatedAt)}`
              : ""}
          </div>
        </div>
      </div>
    </div>
  );
}
