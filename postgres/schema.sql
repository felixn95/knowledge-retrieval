-- noinspection SqlResolveForFile

-- Enable the pgvector extension (if not already enabled)
--- schema
CREATE EXTENSION IF NOT EXISTS vector;

-- Drop and re-create the chunks table
DROP TABLE IF EXISTS chunks;

CREATE TABLE chunks
(
    id            BIGSERIAL PRIMARY KEY,
    content       TEXT,
    document_name VARCHAR(255),
    global_index  INTEGER,
    metadata      JSONB,
    embedding     vector(3072)
);

-- Optional indexes for better performance
CREATE INDEX idx_chunks_document_name ON chunks (document_name);
CREATE INDEX idx_chunks_global_index ON chunks (global_index);

-- Drop and re-create the conversation_session table
DROP TABLE IF EXISTS conversation_session;

CREATE TABLE conversation_session
(
    sessionId     TEXT PRIMARY KEY,
    messages      JSONB,
    chatResponses JSONB,
    queryContexts JSONB,
    feedbacks     JSONB
);