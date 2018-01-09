package com.kdsec.label.repository;

import com.kdsec.label.model.Label;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LabelRepository extends JpaRepository<Label, Long> {
    @Query(value = "SELECT * FROM webpage_label WHERE url = ?1", nativeQuery = true)
    Label findLabelByUrl(String url);
}
