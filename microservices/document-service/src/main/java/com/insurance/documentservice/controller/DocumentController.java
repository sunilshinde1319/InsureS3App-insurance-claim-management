package com.insurance.documentservice.controller;

import com.insurance.documentservice.entity.Document;
import com.insurance.documentservice.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/upload")
    public ResponseEntity<Document> uploadFile(@RequestParam("file") MultipartFile file,
                                               @RequestParam(value = "claimId", required = false) Long claimId,
                                               @RequestParam(value = "pendingReferenceId", required = false) String pendingReferenceId) {
        Document document = fileStorageService.storeFile(file, claimId, pendingReferenceId);
        return ResponseEntity.ok(document);
    }

    @GetMapping("/claim/{claimId}")
    public ResponseEntity<List<Document>> getFilesForClaim(@PathVariable Long claimId) {
        return ResponseEntity.ok(fileStorageService.getFilesByClaimId(claimId));
    }

    @GetMapping("/policy/{policyId}")
    public ResponseEntity<List<Document>> getFilesForPolicy(@PathVariable Long policyId) {
        return ResponseEntity.ok(fileStorageService.getFilesByPolicyId(policyId));
    }

    @PutMapping("/finalize/policy/{pendingReferenceId}/{finalPolicyId}")
    public ResponseEntity<Void> finalizeDocumentsForPolicy(@PathVariable String pendingReferenceId,
                                                           @PathVariable Long finalPolicyId) {
        fileStorageService.finalizeDocumentsForPolicy(pendingReferenceId, finalPolicyId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<Map<String, String>> downloadFile(@PathVariable Long fileId) {
        String fileUrl = fileStorageService.getFileUrl(fileId);
        return ResponseEntity.ok(Map.of("fileUrl", fileUrl));
    }

    @PostMapping("/upload/profile-image")
    public ResponseEntity<Map<String, String>> uploadProfileImage(@RequestParam("file") MultipartFile file) {
        Document document = fileStorageService.storeFile(file, null, null);
        return ResponseEntity.ok(Map.of("fileUrl", document.getFilePath()));
    }

    @GetMapping("/download-by-name/{filename:.+}")
    public ResponseEntity<Map<String, String>> downloadFileByName(@PathVariable String filename) {
        Document document = fileStorageService.getFileByStoredFilename(filename);
        return ResponseEntity.ok(Map.of("fileUrl", document.getFilePath()));
    }
}
