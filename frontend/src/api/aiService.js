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
  chat: async (message) => {
    try {
      const response = await api.post("/ai/chat", { message });
      return response.data;
    } catch (error) {
      console.error("AI Assistant Error:", error);
      throw error;
    }
  }
};
