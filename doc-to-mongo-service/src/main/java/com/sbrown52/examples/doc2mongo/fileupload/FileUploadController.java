package com.sbrown52.examples.doc2mongo.fileupload;

import java.io.IOException;
import java.nio.file.Paths;

import com.sbrown52.examples.doc2mongo.mongo.MongoService;
import com.sbrown52.examples.doc2mongo.storage.StorageService;
import com.sbrown52.examples.doc2mongo.storage.exception.StorageFileNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class FileUploadController {

    Logger logger = LoggerFactory.getLogger(FileUploadController.class.getName());

    private final StorageService storageService;
    private final MongoService mongoService;
    private boolean extractEntities = false;

    @Value("${storage.uploadDirectory}")
    private String uploadDirectory;

    @Autowired
    public FileUploadController(StorageService storageService, MongoService mongoService) {
        this.storageService = storageService;
        this.mongoService = mongoService;
    }

    @GetMapping("/")
    public String listUploadedFiles(Model model) throws IOException {

        model.addAttribute("files", mongoService.getUploadedDocs());
        model.addAttribute("extractEntities", extractEntities);

        return "uploadForm";
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @PostMapping("/")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   @RequestParam(value = "extractEntities", required = false) boolean extractEntities,
                                   RedirectAttributes redirectAttributes) {

        storageService.store(file);

        String fullPathToFile = Paths.get(uploadDirectory, file.getOriginalFilename()).toString();
        try {
            var uploadResult = mongoService.saveDoc(fullPathToFile, extractEntities);
            if (uploadResult.wasAcknowledged()) {
                logger.info("Document uploaded succesfully with ID: {}", uploadResult.getInsertedId());
                redirectAttributes.addFlashAttribute("message",
                        "You successfully uploaded " + file.getOriginalFilename() + "\n_id: " + uploadResult.getInsertedId());
            } else {
                logger.error("Document not acknowledged, check logs for details");
            }

        } catch (Exception e) {
            logger.error("Error uploaded doc", e);
        }
        return "redirect:/";
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

}
