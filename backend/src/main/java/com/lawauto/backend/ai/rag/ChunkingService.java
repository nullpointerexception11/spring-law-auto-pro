package com.lawauto.backend.ai.rag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ChunkingService {

    private static final Logger log = LoggerFactory.getLogger(ChunkingService.class);
    
    private static final int CHUNK_SIZE = 1500;
    private static final int CHUNK_OVERLAP = 200;
    private static final int MIN_CHUNK_SIZE = 200;

    public List<LegalChunk> chunkDocument(
            UUID orgId,
            String sourceType,
            String sourceName,
            String sourceReference,
            UUID sourceDocumentId,
            String fullText) {

        List<String> rawChunks = splitIntoChunks(fullText);
        List<LegalChunk> chunks = new ArrayList<>();

        for (int i = 0; i < rawChunks.size(); i++) {
            LegalChunk chunk = LegalChunk.builder()
                    .orgId(orgId)
                    .sourceType(sourceType)
                    .sourceName(sourceName)
                    .sourceReference(sourceReference)
                    .sourceDocumentId(sourceDocumentId)
                    .chunkIndex(i)
                    .content(rawChunks.get(i).trim())
                    .build();
            chunks.add(chunk);
        }

        log.info("Belge chunklandi: {} -> {} chunk (tur: {})", 
                 sourceName, chunks.size(), sourceType);
        return chunks;
    }

    private List<String> splitIntoChunks(String text) {
        List<String> chunks = new ArrayList<>();
        
        // 1. Once madde/bolum bazinda ayirmayi dene
        String[] sections = trySplitByLegalStructure(text);
        
        if (sections.length > 1) {
            for (String section : sections) {
                if (section.trim().length() >= MIN_CHUNK_SIZE) {
                    chunks.add(section.trim());
                } else if (section.trim().length() > 0 && !chunks.isEmpty()) {
                    String last = chunks.remove(chunks.size() - 1);
                    chunks.add(last + "\n\n" + section.trim());
                }
            }
        } else {
            chunks = recursiveCharacterSplit(text);
        }

        return chunks;
    }

    private String[] trySplitByLegalStructure(String text) {
        String maddeRegex = "(?=\\n\\s*(?:MADDE|Madde)\\s+\\d+\\s*[–-])";
        String[] byArticle = text.split(maddeRegex);
        
        if (byArticle.length > 1) {
            return byArticle;
        }

        String bolumRegex = "(?=\\n\\s*(?:BİRİNCİ|İKİNCİ|ÜÇÜNCÜ|DÖRDÜNCÜ|BEŞİNCİ|ALTINCI|YEDİNCİ|SEKİZİNCİ|DOKUZUNCU|ONUNCU)\\s+BÖLÜM)";
        String[] bySection = text.split(bolumRegex);
        
        if (bySection.length > 1) {
            return bySection;
        }

        return new String[]{text};
    }

    private List<String> recursiveCharacterSplit(String text) {
        List<String> chunks = new ArrayList<>();
        
        if (text.length() <= CHUNK_SIZE) {
            chunks.add(text);
            return chunks;
        }

        String[] paragraphs = text.split("\n\n");
        StringBuilder currentChunk = new StringBuilder();

        for (String paragraph : paragraphs) {
            if (currentChunk.length() + paragraph.length() > CHUNK_SIZE) {
                if (currentChunk.length() >= MIN_CHUNK_SIZE) {
                    chunks.add(currentChunk.toString().trim());
                }
                
                String overlap = "";
                if (!chunks.isEmpty()) {
                    String lastChunk = chunks.get(chunks.size() - 1);
                    overlap = lastChunk.substring(
                        Math.max(0, lastChunk.length() - CHUNK_OVERLAP)
                    ) + "\n\n";
                }
                
                currentChunk = new StringBuilder(overlap);
            }
            
            if (paragraph.length() > CHUNK_SIZE) {
                List<String> sentenceChunks = splitLargeParagraph(paragraph);
                for (String sc : sentenceChunks) {
                    if (currentChunk.length() + sc.length() > CHUNK_SIZE) {
                        chunks.add(currentChunk.toString().trim());
                        currentChunk = new StringBuilder();
                    }
                    currentChunk.append(sc).append(" ");
                }
            } else {
                currentChunk.append(paragraph).append("\n\n");
            }
        }

        if (!currentChunk.isEmpty()) {
            chunks.add(currentChunk.toString().trim());
        }

        return chunks;
    }

    private List<String> splitLargeParagraph(String paragraph) {
        List<String> chunks = new ArrayList<>();
        String[] sentences = paragraph.split("(?<=[.!?])\\s+");
        
        StringBuilder current = new StringBuilder();
        for (String sentence : sentences) {
            if (current.length() + sentence.length() > CHUNK_SIZE) {
                if (current.length() >= MIN_CHUNK_SIZE) {
                    chunks.add(current.toString().trim());
                }
                current = new StringBuilder(sentence);
            } else {
                if (current.length() > 0) current.append(" ");
                current.append(sentence);
            }
        }
        
        if (current.length() >= MIN_CHUNK_SIZE) {
            chunks.add(current.toString().trim());
        } else if (!chunks.isEmpty()) {
            String last = chunks.remove(chunks.size() - 1);
            chunks.add(last + " " + current.toString().trim());
        }
        
        return chunks;
    }
}
