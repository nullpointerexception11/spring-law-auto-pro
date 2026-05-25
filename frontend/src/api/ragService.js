import { api } from "./client";

/**
 * RAG (Retrieval-Augmented Generation) Service
 * Backend'deki /api/rag/* endpoint'leri için client
 */
export const ragService = {
  /**
   * Hybrid search: Anlam + Anahtar Kelime araması
   * @param {string} query - Hukuki arama sorgusu
   * @param {number} limit - Maksimum sonuç sayısı (default: 5, max: 20)
   * @returns {Promise<Array<{chunkId, sourceName, sourceReference, sourceType, content, metadata, score}>>}
   */
  hybridSearch: async (query, limit = 5) => {
    const response = await api.post("/rag/search", { query, limit });
    return response.data;
  },

  /**
   * Semantic (vektör) araması
   */
  semanticSearch: async (query, limit = 5) => {
    const response = await api.post("/rag/search/semantic", { query, limit });
    return response.data;
  },

  /**
   * Keyword (anahtar kelime) araması
   */
  keywordSearch: async (query, limit = 5) => {
    const response = await api.post("/rag/search/keyword", { query, limit });
    return response.data;
  },

  /**
   * Yeni bir hukuki dokümanı indeksle
   * @param {{ sourceType: string, sourceName: string, sourceReference?: string, fullText: string }} document
   */
  indexDocument: async (document) => {
    const response = await api.post("/rag/index", document);
    return response.data;
  },

  /**
   * Organizasyon indeksini temizle
   */
  clearIndex: async () => {
    const response = await api.delete("/rag/clear");
    return response.data;
  },
};
