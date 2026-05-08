package com.lawauto.backend.petition;

import static com.lawauto.backend.petition.PetitionDraftDtos.*;

import java.time.OffsetDateTime;
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

    @Cacheable(value = "petitionDrafts", key = "#matterId")
    public List<PetitionDraftDto> listByMatter(UUID orgId, UUID matterId) {
        log.info("Listing petition drafts for matter [{}] in org [{}]", matterId, orgId);
        String sql = """
                select "id","orgId","matterId","templateId","title","content","sectionValuesJson","status","aiAssistEnabled","aiPrompt","createdByUserId","createdAt","updatedAt"
                from "PetitionDraft"
                where "orgId"=:orgId and "matterId"=:matterId
                order by "createdAt" desc
                """;
        return jdbc.query(sql, new MapSqlParameterSource().addValue("orgId", orgId).addValue("matterId", matterId), (rs, n) ->
                new PetitionDraftDto(
                        rs.getObject("id", UUID.class),
                        rs.getObject("orgId", UUID.class),
                        rs.getObject("matterId", UUID.class),
                        rs.getObject("templateId", UUID.class),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getString("sectionValuesJson"),
                        rs.getString("status"),
                        rs.getBoolean("aiAssistEnabled"),
                        rs.getString("aiPrompt"),
                        rs.getObject("createdByUserId", UUID.class),
                        rs.getObject("createdAt", OffsetDateTime.class),
                        rs.getObject("updatedAt", OffsetDateTime.class)
                ));
    }

    @Transactional
    @CacheEvict(value = "petitionDrafts", key = "#req.matterId()")
    public UUID create(CreatePetitionDraftRequest req) {
        log.info("Creating new petition draft [{}] for matter [{}]", req.title(), req.matterId());
        UUID id = UUID.randomUUID();
        String sql = """
                insert into "PetitionDraft" ("id","orgId","matterId","templateId","title","content","sectionValuesJson","status","aiAssistEnabled","aiPrompt","createdByUserId","updatedAt")
                values (:id,:orgId,:matterId,:templateId,:title,:content,:sectionValuesJson,'DRAFT',:aiAssistEnabled,:aiPrompt,:createdByUserId,now())
                """;
        jdbc.update(sql, new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("orgId", req.orgId())
                .addValue("matterId", req.matterId())
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
    @CacheEvict(value = "petitionDrafts", key = "#matterId")
    public void update(UUID orgId, UUID matterId, UUID draftId, UpdatePetitionDraftRequest req) {
        log.info("Updating petition draft [{}] for matter [{}]", draftId, matterId);
        String sql = """
                update "PetitionDraft"
                set "title"=coalesce(:title,"title"),
                    "content"=coalesce(:content,"content"),
                    "sectionValuesJson"=coalesce(:sectionValuesJson,"sectionValuesJson"),
                    "status"=coalesce(:status,"status"),
                    "aiAssistEnabled"=coalesce(:aiAssistEnabled,"aiAssistEnabled"),
                    "aiPrompt"=coalesce(:aiPrompt,"aiPrompt"),
                    "updatedAt"=now()
                where "id"=:id and "orgId"=:orgId and "matterId"=:matterId
                """;
        int updated = jdbc.update(sql, new MapSqlParameterSource()
                .addValue("id", draftId)
                .addValue("orgId", orgId)
                .addValue("matterId", matterId)
                .addValue("title", req.title())
                .addValue("content", req.content())
                .addValue("sectionValuesJson", req.sectionValuesJson())
                .addValue("status", req.status())
                .addValue("aiAssistEnabled", req.aiAssistEnabled())
                .addValue("aiPrompt", req.aiPrompt()));
        if (updated == 0) throw new IllegalArgumentException("Petition draft not found");
    }
}
