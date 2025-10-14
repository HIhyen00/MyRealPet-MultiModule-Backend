package petlifecycle.core.metadata.repository;

import petlifecycle.dto.metadata.entity.MetaDataFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MetaDataFileRepository extends JpaRepository<MetaDataFile, Long> {
    Optional<MetaDataFile> findByS3Key(String s3Key);
    List<MetaDataFile> findByIsDeletedTrueAndS3DeletedFalseAndDeletedAtBefore(LocalDateTime cutOfDate);
    Optional<MetaDataFile> findById(Long id);
}
