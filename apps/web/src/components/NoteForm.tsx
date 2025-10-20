import { useState, useEffect } from "react";

import { type Note } from "../types/Note";

interface NoteFormProps {
  note?: Note;
  onSubmit: (note: Partial<Note>) => void;
  onCancel: () => void;
}

export default function NoteForm({ note, onSubmit, onCancel }: NoteFormProps) {
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [visibility, setVisibility] = useState("private");
  const [alertAt, setAlertAt] = useState("");

  // Initialize form when editing an existing note
  useEffect(() => {
    if (note) {
      setTitle(note.title);
      setContent(note.content);
      setVisibility(note.visibility);
      if (note.alertAt) {
        // Convert to local date-time format for input
        const date = new Date(note.alertAt);
        setAlertAt(date.toISOString().slice(0, 16));
      }
    }
  }, [note]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    // Basic validation
    if (!title.trim()) {
      return;
    }

    const formData: Partial<Note> = {
      title,
      content,
      visibility,
    };

    // For new notes, add required fields
    if (!note) {
      formData.serverId = 0; // Default or get from context if available
      formData.channelId = 0; // Default or get from context if available
    }

    if (alertAt) {
      formData.alertAt = new Date(alertAt).toISOString();
    }

    onSubmit(formData);
  };

  return (
    <div className="bg-gray-800 rounded-lg shadow-lg p-6 border border-gray-700">
      <h2 className="text-xl font-bold mb-4 text-white">
        {note ? "Edit Note" : "Create New Note"}
      </h2>
      <form onSubmit={handleSubmit}>
        <div className="mb-4">
          <label
            htmlFor="title"
            className="block text-sm font-medium text-gray-300 mb-1"
          >
            Title
          </label>
          <input
            id="title"
            type="text"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            className="w-full bg-gray-700 border border-gray-600 rounded-md py-2 px-3 text-white placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            placeholder="Enter a title for your note"
            required
          />
        </div>

        <div className="mb-4">
          <label
            htmlFor="content"
            className="block text-sm font-medium text-gray-300 mb-1"
          >
            Content
          </label>
          <textarea
            id="content"
            value={content}
            onChange={(e) => setContent(e.target.value)}
            rows={6}
            className="w-full bg-gray-700 border border-gray-600 rounded-md py-2 px-3 text-white placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            placeholder="Write your note content here..."
          />
        </div>

        <div className="mb-4">
          <label
            htmlFor="visibility"
            className="block text-sm font-medium text-gray-300 mb-1"
          >
            Visibility
          </label>
          <select
            id="visibility"
            value={visibility}
            onChange={(e) => setVisibility(e.target.value)}
            className="w-full bg-gray-700 border border-gray-600 rounded-md py-2 px-3 text-white focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          >
            <option value="private">Private</option>
            <option value="public">Public</option>
          </select>
        </div>

        <div className="mb-6">
          <label
            htmlFor="alertAt"
            className="block text-sm font-medium text-gray-300 mb-1"
          >
            Reminder (Optional)
          </label>
          <input
            id="alertAt"
            type="datetime-local"
            value={alertAt}
            onChange={(e) => setAlertAt(e.target.value)}
            className="w-full bg-gray-700 border border-gray-600 rounded-md py-2 px-3 text-white focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          />
        </div>

        <div className="flex justify-end space-x-3">
          <button
            type="button"
            onClick={onCancel}
            className="px-4 py-2 bg-gray-700 text-white rounded-md hover:bg-gray-600 transition-colors"
          >
            Cancel
          </button>
          <button
            type="submit"
            className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-500 transition-colors"
          >
            {note ? "Save Changes" : "Create Note"}
          </button>
        </div>
      </form>
    </div>
  );
}
