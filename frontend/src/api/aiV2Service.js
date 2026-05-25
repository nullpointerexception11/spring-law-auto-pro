import { api } from "./client";

/**
 * AI v2 Service - Backend'deki /api/ai/v2/* endpoint'leri
 * 
 * 4 ana prensibi destekler:
 * 1. RAG (Retrieval-Augmented Generation)
 * 2. Tool Calling (Agent)
 * 3. Model Routing (GPT-4o-mini / GPT-4o)
 * 4. Security (Data Masking)
 */
export const aiV2Service = {
  /**
   * Gelişmiş AI Chat (RAG + Model Routing + Data Masking)
   * @param {string} message - Kullanıcı mesajı
   * @param {string} conversationId - Sohbet oturum ID'si
   * @returns {Promise<{reply: string, modelUsed?: string, sources?: Array}>}
   */
  chat: async (message, conversationId = "default-session") => {
    const response = await api.post("/ai/v2/chat", { message, conversationId });
    return response.data;
  },

  /**
   * Stream yanıt (SSE - Server-Sent Events)
   * @param {string} message - Kullanıcı mesajı
   * @param {Function} onChunk - Her yeni metin parçası için callback
   * @param {AbortSignal} signal - İsteği iptal etmek için
   * @param {string} conversationId - Sohbet oturum ID'si
   */
  chatStream: async (message, onChunk, signal, conversationId = "default-session") => {
    const token = localStorage.getItem("token");
    const baseUrl = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api";
    
    const response = await fetch(`${baseUrl}/ai/v2/chat/stream`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify({ message, conversationId }),
      signal
    });

    if (!response.ok) throw new Error("AI v2 Stream Error");

    const reader = response.body.getReader();
    const decoder = new TextDecoder();

    while (true) {
      const { done, value } = await reader.read();
      if (done) break;
      const chunk = decoder.decode(value, { stream: true });
      onChunk(chunk);
    }
  },

  /**
   * Sadece RAG araması (AI yanıtsız)
   * @param {string} query - Hukuki arama sorgusu
   * @returns {Promise<Array>}
   */
  search: async (query) => {
    const response = await api.post("/ai/v2/search", { query });
    return response.data;
  },
};
