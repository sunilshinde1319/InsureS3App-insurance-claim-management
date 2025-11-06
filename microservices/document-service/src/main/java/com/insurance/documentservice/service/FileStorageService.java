package com.insurance.documentservice.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.insurance.documentservice.entity.Document;
import com.insurance.documentservice.repository.DocumentRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@Service
public class FileStorageService {

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.api-key}")
    private String apiKey;

    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    private Cloudinary cloudinary;
    private final DocumentRepository documentRepository;

    public FileStorageService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @PostConstruct
    public void init() {
        cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true
        ));
    }

    /**
     * Upload file to Cloudinary and store metadata in database.
     */
    public Document storeFile(MultipartFile file, Long claimId, String pendingReferenceId) {
        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());

            Document document = new Document();
            document.setOriginalFilename(file.getOriginalFilename());
            document.setStoredFilename(uploadResult.get("public_id").toString());
            document.setContentType(file.getContentType());
            document.setFilePath(uploadResult.get("secure_url").toString());

            if (claimId != null) {
                document.setClaimId(claimId);
            } else {
                document.setPendingReferenceId(pendingReferenceId);
            }

            return documentRepository.save(document);
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file. Please try again!", ex);
        }
    }

    @Transactional
    public void finalizeDocumentsForPolicy(String pendingReferenceId, Long finalPolicyId) {
        List<Document> documentsToUpdate = documentRepository.findByPendingReferenceId(pendingReferenceId);
        for (Document doc : documentsToUpdate) {
            doc.setPendingReferenceId(null);
            doc.setPolicyId(finalPolicyId);
        }
        documentRepository.saveAll(documentsToUpdate);
    }

    public List<Document> getFilesByPolicyId(Long policyId) {
        return documentRepository.findByPolicyId(policyId);
    }

    public List<Document> getFilesByClaimId(Long claimId) {
        return documentRepository.findByClaimId(claimId);
    }

    public String getFileUrl(Long fileId) {
        Document document = documentRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found with id " + fileId));
        return document.getFilePath();
    }

    public Document getFileByStoredFilename(String storedFilename) {
        return documentRepository.findByStoredFilename(storedFilename)
                .orElseThrow(() -> new RuntimeException("File not found with name " + storedFilename));
    }

    public Document getFile(Long fileId) {
        return documentRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found with id " + fileId));
    }

    /**
     * Dummy local resource loader (not used for Cloudinary).
     * Only kept for compatibility with existing endpoints.
     */
    public Resource loadFileAsResource(String filename) {
        try {
            Path fakePath = Path.of(filename);
            return new UrlResource(fakePath.toUri());
        } catch (MalformedURLException e) {
            throw new RuntimeException("File not found: " + filename, e);
        }
    }
}
