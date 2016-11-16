package com.photogram;

import com.photogram.Repository.PhotoRepository;
import com.photogram.Repository.UserRepository;
import com.photogram.User;
import com.photogram.Storage.StorageFileNotFoundException;
import com.photogram.Storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Fredrik on 15.11.2016.
 */
@Controller
public class FileUploadController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PhotoRepository photoRepository;

    private final StorageService storageService;

    @Autowired
    public FileUploadController(StorageService storageService) {
        this.storageService = storageService;
    }


    @GetMapping("/photoadmin")
    public String listUploadedFiles(Model model) throws IOException {

        model.addAttribute("files", storageService
                .loadAll()
                .map(path ->
                        MvcUriComponentsBuilder
                                .fromMethodName(FileUploadController.class, "serveFile", path.getFileName().toString())
                                .build().toString())
                .collect(Collectors.toList()));

        return "photoadmin";
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }

    @PostMapping("/addphoto")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {

        Photo p = new Photo();
        p.setFilnavn("/files/"+file.getOriginalFilename());
        p.setContentType(file.getContentType());
        p.setPhotographerID("?");
        p.setDato("?");
        p.setTag("?");
        p.setTittel("?");
        photoRepository.save(p);

        storageService.store(file);
        redirectAttributes.addFlashAttribute("message",
                "Du lastet opp " + file.getOriginalFilename() + "!");

        return "redirect:photoadmin";
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

    @RequestMapping(path = "/list")
    public String visAlle(ModelMap map){
        List<Photo> filList = photoRepository.findAll();
        map.addAttribute("liste", filList);
        return "list";
    }

    @RequestMapping(path = "/delete/{id}", method = RequestMethod.POST)
    public @ResponseBody void delete(@PathVariable("id") String id){
        photoRepository.delete(id);
    }
}