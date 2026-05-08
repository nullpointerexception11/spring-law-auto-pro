package com.lawauto.backend.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FileFolderRepository extends JpaRepository<FileFolder, UUID> {
}
