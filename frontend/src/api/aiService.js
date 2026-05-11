import { api } from "./client";

/**
 * AI Service to interact with the Spring AI Assistant
 */
export const aiService = {
  /**
   * Sends a message to the AI assistant and receives a reply.
   * @param {string} message The user prompt
   * @returns {Promise<{reply: string}>} The assistant's response
   */
  chat: async (message, conversationId = "default-session") => {
    const response = await api.post("/ai/chat", { message, conversationId });
    return response.data;
  },

  /**
   * Streams a message to the AI assistant.
   * @param {string} message The user prompt
   * @param {Function} onChunk Callback for each new text chunk
   * @param {AbortSignal} signal Signal to cancel the request
   * @param {string} conversationId Session identifier for history
   */
  chatStream: async (message, onChunk, signal, conversationId = "default-session") => {
    const token = localStorage.getItem("token");
    const response = await fetch(`${import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api"}/ai/chat/stream`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify({ message, conversationId }),
      signal // Item 5: AbortController support
    });

    if (!response.ok) throw new Error("AI Stream Error");

    const reader = response.body.getReader();
    const decoder = new TextDecoder();

    while (true) {
      const { done, value } = await reader.read();
      if (done) break;
      const chunk = decoder.decode(value, { stream: true });
      onChunk(chunk);
    }
  }
};
