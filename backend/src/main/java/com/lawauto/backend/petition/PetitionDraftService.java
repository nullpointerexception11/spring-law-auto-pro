package com.lawauto.backend.petition;

import static com.lawauto.backend.petition.PetitionDraftDtos.*;

import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class PetitionDraftService {
    private final NamedParameterJdbcTemplate jdbc;

    public PetitionDraftService(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Cacheable(value = "petitionDrafts", key = "#caseId")
    public List<PetitionDraftDto> listByCase(UUID orgId, UUID caseId) {
        log.info("Listing petition drafts for case [{}] in org [{}]", caseId, orgId);
        String sql = """
                select "id","orgId","caseId","templateId","title","content","sectionValuesJson","status","aiAssistEnabled","aiPrompt","createdByUserId","createdAt","updatedAt"
                from "PetitionDraft"
                where "orgId"=:orgId and "caseId"=:caseId
                order by "createdAt" desc
                """;
        return jdbc.query(sql, new MapSqlParameterSource().addValue("orgId", orgId).addValue("caseId", caseId), (rs, n) ->
                new PetitionDraftDto(
                        rs.getObject("id", UUID.class),
                        rs.getObject("orgId", UUID.class),
                        rs.getObject("caseId", UUID.class),
                        rs.getObject("templateId", UUID.class),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getString("sectionValuesJson"),
                        rs.getString("status"),
                        rs.getBoolean("aiAssistEnabled"),
                        rs.getString("aiPrompt"),
                        rs.getObject("createdByUserId", UUID.class),
                        rs.getTimestamp("createdAt").toLocalDateTime(),
                        rs.getTimestamp("updatedAt").toLocalDateTime()
                ));
    }

    @Transactional
    @CacheEvict(value = "petitionDrafts", key = "#req.caseId()")
    public UUID create(CreatePetitionDraftRequest req) {
        log.info("Creating new petition draft [{}] for case [{}]", req.title(), req.caseId());
        UUID id = UUID.randomUUID();
        String sql = """
                insert into "PetitionDraft" ("id","orgId","caseId","templateId","title","content","sectionValuesJson","status","aiAssistEnabled","aiPrompt","createdByUserId","updatedAt")
                values (:id,:orgId,:caseId,:templateId,:title,:content,:sectionValuesJson,'DRAFT',:aiAssistEnabled,:aiPrompt,:createdByUserId,now())
                """;
        jdbc.update(sql, new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("orgId", req.orgId())
                .addValue("caseId", req.caseId())
                .addValue("templateId", req.templateId())
                .addValue("title", req.title())
                .addValue("content", req.content())
                .addValue("sectionValuesJson", req.sectionValuesJson())
                .addValue("aiAssistEnabled", Boolean.TRUE.equals(req.aiAssistEnabled()))
                .addValue("aiPrompt", req.aiPrompt())
                .addValue("createdByUserId", req.createdByUserId()));
        return id;
    }

    @Transactional
    @CacheEvict(value = "petitionDrafts", key = "#caseId")
    public void update(UUID orgId, UUID caseId, UUID draftId, UpdatePetitionDraftRequest req) {
        log.info("Updating petition draft [{}] for case [{}]", draftId, caseId);
        String sql = """
                update "PetitionDraft"
                set "title"=coalesce(:title,"title"),
                    "content"=coalesce(:content,"content"),
                    "sectionValuesJson"=coalesce(:sectionValuesJson,"sectionValuesJson"),
                    "status"=coalesce(:status,"status"),
                    "aiAssistEnabled"=coalesce(:aiAssistEnabled,"aiAssistEnabled"),
                    "aiPrompt"=coalesce(:aiPrompt,"aiPrompt"),
                    "updatedAt"=now()
                where "id"=:id and "orgId"=:orgId and "caseId"=:caseId
                """;
        int updated = jdbc.update(sql, new MapSqlParameterSource()
                .addValue("id", draftId)
                .addValue("orgId", orgId)
                .addValue("caseId", caseId)
                .addValue("title", req.title())
                .addValue("content", req.content())
                .addValue("sectionValuesJson", req.sectionValuesJson())
                .addValue("status", req.status())
                .addValue("aiAssistEnabled", req.aiAssistEnabled())
                .addValue("aiPrompt", req.aiPrompt()));
        if (updated == 0) throw new IllegalArgumentException("Petition draft not found");
    }
}
