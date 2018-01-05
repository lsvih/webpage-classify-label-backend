package com.kdsec.label.controller;

import com.kdsec.label.model.Label;
import com.kdsec.label.repository.LabelRepository;
import com.sun.xml.internal.ws.client.sei.ResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.xml.ws.Response;
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
    public ResponseEntity<Label> createLabel(HttpServletRequest request, @Valid @RequestBody Label label) {
        label.setIP(request.getRemoteAddr());
        Label saveLabel;
        try {
            saveLabel = labelRepository.save(label);
        } catch (Exception e) {
            ResponseEntity error = ResponseEntity
                    .status(409)
                    .body("{\"message\":\"此 url 已存在\",\"CODE\":\"409\",\"info\":\"" + e.getMessage() + "\"}");
            return error;
        }
        return ResponseEntity.ok(saveLabel);
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
