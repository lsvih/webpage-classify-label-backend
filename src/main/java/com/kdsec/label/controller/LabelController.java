package com.kdsec.label.controller;

import com.kdsec.label.model.Label;
import com.kdsec.label.repository.LabelRepository;
import com.kdsec.label.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/")
public class LabelController {

    @Autowired
    LabelRepository labelRepository;

    @GetMapping("/labels")
    public List<Label> getAllLabels() {
        return labelRepository.findAll();
    }

    @GetMapping("/ping")
    public String testState() {
        return "OK";
    }

    @GetMapping("/labels/{id}")
    public ResponseEntity<Label> getLabelById(@PathVariable(value = "id") Long labelId) {
        Label label = labelRepository.findOne(labelId);
        if (label == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(label);
    }

    @PostMapping("/labels")
    public ResponseEntity createLabel(HttpServletRequest request, @Valid @RequestBody Label label) {
        label.setIP(request.getRemoteAddr());
        try {
            labelRepository.save(label);
            FileService file = new FileService();
            file.save(label.getId(), label.getHtml(), label.getImage());
        } catch (Exception e) {
            ResponseEntity error = ResponseEntity
                    .status(409)
                    .body("{\"message\":\"" + e.getMessage() + "\",\"CODE\":\"409\",\"}");
            return error;
        }
        return ResponseEntity.ok().build();
    }

    @PutMapping("/labels/{id}")
    public ResponseEntity<Label> updateLabel(@PathVariable(value = "id") Long labelId,
                                             @Valid @RequestBody Label labelDetails) {
        Label label = labelRepository.findOne(labelId);
        if (label == null) {
            return ResponseEntity.notFound().build();
        }
        label.setUrl(labelDetails.getUrl());
        label.setClazz(labelDetails.getClazz());
        Label updatedLabel = labelRepository.save(label);
        return ResponseEntity.ok(updatedLabel);
    }

    @DeleteMapping("/labels/{id}")
    public ResponseEntity<Label> deleteLabel(@PathVariable(value = "id") Long labelId) {
        Label label = labelRepository.findOne(labelId);
        if (label == null) {
            return ResponseEntity.notFound().build();
        }

        labelRepository.delete(label);
        return ResponseEntity.ok().build();
    }
}
