import { useEffect, useState } from "react";

import api from "../services/api";
import { connectWebSocket, disconnectWebSocket } from "../services/websocket";

interface Note {
  id: string;
  title: string;
  content: string;
  createdAt: string;
  updatedAt: string;
}

export default function NoteList() {
  const [notes, setNotes] = useState<Note[]>([]);
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");

  useEffect(() => {
    fetchNotes();

    connectWebSocket((note: Note) => {
      setNotes((prev) => {
        const exists = prev.find((n) => n.id === note.id);

        if (exists) {
          return prev.map((n) => (n.id === note.id ? note : n));
        } else {
          return [...prev, note];
        }
      });
    });

    return () => disconnectWebSocket();
  }, []);

  const fetchNotes = async () => {
    const res = await api.get<Note[]>("/notes");
    setNotes(res.data);
  };

  const createNote = async () => {
    if (!title.trim() || !content.trim()) return;

    await api.post("/notes", { title, content });

    setTitle("");
    setContent("");
  };

  const deleteNote = async (id: string) => {
    await api.delete(`/notes/${id}`);

    setNotes((prev) => prev.filter((n) => n.id !== id));
  };

  return (
    <div className="max-w-2xl mx-auto p-6">
      <h1 className="text-3xl font-bold mb-6 text-center">📝 Minhas Notas</h1>

      <div className="mb-6 p-4 border rounded shadow-sm flex flex-col gap-2">
        <input
          className="border p-2 rounded"
          placeholder="Título"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
        />
        <textarea
          className="border p-2 rounded"
          placeholder="Conteúdo"
          value={content}
          onChange={(e) => setContent(e.target.value)}
        />
        <button
          className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600 transition"
          onClick={createNote}
        >
          Criar Nota
        </button>
      </div>

      <ul className="space-y-3">
        {notes.map((note) => (
          <li
            key={note.id}
            className="p-4 border rounded flex justify-between items-start shadow-sm hover:shadow-md transition"
          >
            <div>
              <p className="font-bold">{note.title}</p>
              <p className="text-gray-700">{note.content}</p>
              <p className="text-xs text-gray-400">
                Criado: {new Date(note.createdAt).toLocaleString()} |
                Atualizado: {new Date(note.updatedAt).toLocaleString()}
              </p>
            </div>
            <button
              className="text-red-500 font-bold hover:text-red-700 transition"
              onClick={() => deleteNote(note.id)}
            >
              X
            </button>
          </li>
        ))}
      </ul>
    </div>
  );
}
