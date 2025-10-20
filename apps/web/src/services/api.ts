import axios from "axios";
import type { Note } from "../types/Note";

const BASE_URL = import.meta.env.VITE_API_BASE_URL;

const api = axios.create({
  baseURL: BASE_URL,
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

export const authApi = {
  exchangeCode: (code: string) => {
    const redirect_uri = `${window.location.origin}/auth/callback`;

    return api.post("/auth/exchange", { code, redirect_uri });
  },
};

export const notesApi = {
  getUserNotes: (userId: string) => {
    return api.get<Note[]>(`/notes/user/${userId}`);
  },

  getNoteById: (id: string) => {
    return api.get<Note>(`/notes/${id}`);
  },

  createNote: (
    note: Omit<Note, "id" | "createdAt" | "updatedAt" | "discordUserId">,
  ) => {
    return api.post<Note>("/notes", note);
  },

  updateNote: (id: string, note: Partial<Note>) => {
    return api.put<Note>(`/notes/${id}`, note);
  },

  deleteNote: (id: string) => {
    return api.delete(`/notes/${id}`);
  },
};

export default api;
