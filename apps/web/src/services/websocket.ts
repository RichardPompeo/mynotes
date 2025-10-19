export interface Note {
  id: string;
  title: string;
  content: string;
  createdAt: string;
  updatedAt: string;
}

let ws: WebSocket | null = null;

export const connectWebSocket = (onMessageReceived: (note: Note) => void) => {
  ws = new WebSocket("ws://localhost:8080/ws");

  ws.addEventListener("open", () => {
    console.log("✅ Conectado ao WebSocket");
  });

  ws.addEventListener("message", (event) => {
    try {
      const note: Note = JSON.parse(event.data);
      onMessageReceived(note);
    } catch (err) {
      console.error(err);
    }
  });

  ws.addEventListener("close", () => {
    console.log("🔌 Desconectado do WebSocket");
    ws = null;
  });

  ws.addEventListener("error", (err) => {
    console.error("Erro WebSocket:", err);
  });
};

export const disconnectWebSocket = () => {
  if (ws) {
    ws.close();
    ws = null;
  }
};
