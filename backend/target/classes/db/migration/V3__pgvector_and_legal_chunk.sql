-- ==========================================
-- PGVECTOR + LEGAL_CHUNK TABLOSU
-- RAG (Retrieval-Augmented Generation) için
-- ==========================================

-- 1. pgvector extension'ını etkinleştir
CREATE EXTENSION IF NOT EXISTS vector;

-- 2. LegalChunk tablosu (hukuki kaynakların chunk'ları)
CREATE TABLE legal_chunk (
    id UUID PRIMARY KEY,
    org_id UUID NOT NULL REFERENCES "org"("id") ON DELETE CASCADE,
    source_type VARCHAR(50) NOT NULL,
    source_name TEXT NOT NULL,
    source_reference TEXT,
    source_document_id UUID REFERENCES "file_object"("id") ON DELETE SET NULL,
    chunk_index INT NOT NULL,
    content TEXT NOT NULL,
    embedding vector(1536),      -- OpenAI text-embedding-ada-002 (1536 boyut)
    search_vector tsvector,      -- Full-text search için
    metadata TEXT,               -- JSON olarak ek bilgiler
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 3. İNDEX'LER

-- Vektör araması için IVFFlat index (pgvector)
-- lists = sqrt(rows) önerilir, 100 varsayılan
CREATE INDEX idx_legal_chunk_embedding 
    ON legal_chunk 
    USING ivfflat (embedding vector_cosine_ops)
    WITH (lists = 100);

-- Full-text search index (GIN)
CREATE INDEX idx_legal_chunk_search_vector 
    ON legal_chunk 
    USING GIN (search_vector);

-- Organizasyon bazında arama için
CREATE INDEX idx_legal_chunk_org_id 
    ON legal_chunk (org_id);

-- Kaynak türüne göre filtreleme
CREATE INDEX idx_legal_chunk_source_type 
    ON legal_chunk (source_type);

-- Organizasyon + tür kombinasyonu
CREATE INDEX idx_legal_chunk_org_source 
    ON legal_chunk (org_id, source_type);

-- Doküman bazında chunk getirme
CREATE INDEX idx_legal_chunk_document 
    ON legal_chunk (source_document_id, chunk_index);

-- 4. Otomatik tsvector güncelleme trigger'ı
CREATE OR REPLACE FUNCTION update_legal_chunk_search_vector()
RETURNS TRIGGER AS $$
BEGIN
    NEW.search_vector = to_tsvector('turkish', COALESCE(NEW.content, ''));
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_legal_chunk_search_vector
    BEFORE INSERT OR UPDATE OF content
    ON legal_chunk
    FOR EACH ROW
    EXECUTE FUNCTION update_legal_chunk_search_vector();

-- 5. Embedding güncellendiğinde updated_at güncelleme trigger'ı
CREATE OR REPLACE FUNCTION update_legal_chunk_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_legal_chunk_timestamp
    BEFORE UPDATE OF embedding, content, metadata
    ON legal_chunk
    FOR EACH ROW
    EXECUTE FUNCTION update_legal_chunk_timestamp();
