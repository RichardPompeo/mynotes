import { useState, useEffect } from "react";
import { useNavigate } from "react-router";
import { Plus, FileText } from "lucide-react";

import { useAuth } from "../hooks/useAuth";
import { notesApi } from "../services/api";
import Layout from "../components/Layout";
import NoteCard from "../components/NoteCard";
import NoteForm from "../components/NoteForm";
import Spinner from "../components/Spinner";
import type { Note } from "../types/Note";

export default function Dashboard() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [notes, setNotes] = useState<Note[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [showForm, setShowForm] = useState(false);
  const [editingNote, setEditingNote] = useState<Note | null>(null);

  useEffect(() => {
    const fetchNotes = async () => {
      if (!user?.id) return;

      try {
        setLoading(true);

        const response = await notesApi.getUserNotes(user.id);

        setNotes(response.data);
        setError(null);
      } catch (err) {
        console.error("Failed to fetch notes:", err);
        setError("Failed to load notes. Please try again later.");
      } finally {
        setLoading(false);
      }
    };

    fetchNotes();
  }, [user]);

  const handleCreateNote = async (noteData: Partial<Note>) => {
    if (!user?.id) return;

    try {
      setLoading(true);

      const response = await notesApi.createNote({
        title: noteData.title as string,
        content: noteData.content ?? "",
        visibility: noteData.visibility ?? "private",
        serverId: noteData.serverId ?? 0,
        channelId: noteData.channelId ?? 0,
        alertAt: noteData.alertAt,
      });

      setNotes([...notes, response.data]);
      setShowForm(false);
      setEditingNote(null);
    } catch (err) {
      console.error("Failed to create note:", err);
      setError("Failed to create note. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  const handleUpdateNote = async (updatedData: Partial<Note>) => {
    if (!editingNote?.id) return;

    try {
      setLoading(true);
      const response = await notesApi.updateNote(editingNote.id, updatedData);

      setNotes(
        notes.map((note) =>
          note.id === editingNote.id ? response.data : note,
        ),
      );

      setEditingNote(null);
    } catch (err) {
      console.error("Failed to update note:", err);
      setError("Failed to update note. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteNote = async (noteId: string) => {
    if (!window.confirm("Are you sure you want to delete this note?")) {
      return;
    }

    try {
      setLoading(true);
      await notesApi.deleteNote(noteId);
      setNotes(notes.filter((note) => note.id !== noteId));
    } catch (err) {
      console.error("Failed to delete note:", err);
      setError("Failed to delete note. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  const handleEditNote = (note: Note) => {
    setEditingNote(note);
    setShowForm(true);
  };

  const handleViewNote = (noteId: string) => {
    navigate(`/notes/${noteId}`);
  };

  const handleFormSubmit = (noteData: Partial<Note>) => {
    if (editingNote) {
      handleUpdateNote(noteData);
    } else {
      handleCreateNote(noteData);
    }
  };

  const handleFormCancel = () => {
    setShowForm(false);
    setEditingNote(null);
  };

  return (
    <Layout title="My Notes Dashboard">
      <div className="mb-6 flex justify-between items-center">
        <h1 className="text-2xl font-bold text-white">Your Notes</h1>

        <button
          onClick={() => {
            setEditingNote(null);
            setShowForm(true);
          }}
          className="flex items-center gap-2 bg-blue-600 hover:bg-blue-500 text-white px-4 py-2 rounded-md transition-colors"
        >
          <Plus size={20} />
          New Note
        </button>
      </div>

      {error && (
        <div className="bg-red-900/20 border border-red-500 rounded-md p-4 mb-6">
          <p className="text-red-300">{error}</p>
        </div>
      )}

      {showForm && (
        <div className="mb-6">
          <NoteForm
            note={editingNote || undefined}
            onSubmit={handleFormSubmit}
            onCancel={handleFormCancel}
          />
        </div>
      )}

      {loading && !notes.length ? (
        <div className="flex justify-center my-12">
          <Spinner size="large" />
        </div>
      ) : notes.length ? (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {notes.map((note) => (
            <div
              key={note.id}
              onClick={() => handleViewNote(note.id)}
              className="cursor-pointer"
            >
              <NoteCard
                note={note}
                onEdit={handleEditNote}
                onDelete={handleDeleteNote}
              />
            </div>
          ))}
        </div>
      ) : (
        <div className="text-center my-12 py-12 bg-gray-800 rounded-lg border border-gray-700">
          <FileText size={48} className="mx-auto mb-4 text-gray-600" />
          <h3 className="text-xl font-semibold text-gray-300 mb-2">
            No Notes Yet
          </h3>
          <p className="text-gray-500">
            You don't have any notes yet. Click "New Note" to create your first
            note.
          </p>
        </div>
      )}
    </Layout>
  );
}
