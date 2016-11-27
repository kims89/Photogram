package com.photogram;

import com.photogram.POJO.Comments;
import com.photogram.POJO.Photo;
import com.photogram.POJO.User;
import com.photogram.Repository.CommentsRepository;
import com.photogram.Repository.PhotoRepository;
import com.photogram.Repository.UserRepository;
import com.photogram.Storage.StorageFileNotFoundException;
import com.photogram.Storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Controller
public class FileUploadController {

    @Autowired
    CommentsRepository commentsrepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PhotoRepository photoRepository;

    private final StorageService storageService;

    @Autowired
    public FileUploadController(StorageService storageService) {
        this.storageService = storageService;
    }


    @RequestMapping(value = "/photoadmin", method = RequestMethod.GET)
    public String homeAdmin(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User u = userRepository.findByBrukernavn(auth.getName());
        List<Photo> photoList = new ArrayList<Photo>();

        for (Photo ph : photoRepository.findAll()) {
            if (ph.getPhotographerID() != null && ph.getPhotographerID().contains(u.getId())) {
                photoList.add(ph);
            }
        }
        Collections.reverse(photoList);
        model.addAttribute("photo", photoList);

        return "photoadmin";
    }

    @RequestMapping(value = "/files/{filename:.+}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }

    @RequestMapping(value = "/PAAddPhoto", method = RequestMethod.POST)
    public String handleFileUpload(@RequestParam("bild") MultipartFile file, @RequestParam("tittel") String tittel,
                                   @RequestParam("dato") String dato, @RequestParam("beskrivelse") String beskrivelse) throws Exception {

        String brukerid = "";
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        for (User phgr : userRepository.findAll()) {
            if (phgr.getBrukernavn() != null && phgr.getBrukernavn().contains(auth.getName())) {
                brukerid = phgr.getId();

            }
        }

        makeFolder();
        List<String> tagList = new ArrayList<String>();
        List<Photo> photoDList = new ArrayList<Photo>();
        List<Comments> commentsList = new ArrayList<Comments>();
        Photo p = new Photo();


        System.out.println("før");
        p.setFilnavn("/files/" + file.getOriginalFilename());
        System.out.println("etter");
        p.setContentType(file.getContentType());
        p.setDato(dato);
        p.setBeskrivelse(beskrivelse);
        p.setTag(tagList);
        p.setTittel(tittel);
        p.setPhotographerID(brukerid);
        p.setKommentarer(commentsList);
        System.out.println("Filen finnes" + file.getOriginalFilename());

        if (file.getContentType().contains("image")) {
            File y = new File("upload-dir/" + file.getOriginalFilename());
            if (!y.exists()) {
                storageService.store(file);
                photoRepository.save(p);
                User user = userRepository.findOne(brukerid);
                for (Photo ph : photoRepository.findAll()) {
                    if (ph.getPhotographerID() != null && ph.getPhotographerID().contains(brukerid)) {
                        photoDList.add(ph);
                    }
                }
                System.out.println("Filen finnes, derfor lager man ikke en ny");
            } else {
                throw new Exception("ds");
            }
        }

        return "redirect:photoadmin";
    }


    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

    @RequestMapping(path = "/delete/{id}", method = RequestMethod.POST)
    public
    @ResponseBody
    void delete(@PathVariable("id") String id) {
        System.out.println(id);
        Photo p = photoRepository.findOne(id);
        photoRepository.delete(id);
        for (Comments c : commentsrepository.findAll()) {
            if (c.getPhotoID().equals(id)) {
                commentsrepository.delete(c);
            }

        }


        String filnavn = p.getFilnavn();

        File file = new File("upload-dir/" + filnavn.replace("/files/", ""));
        file.delete();

    }

    public void makeFolder() {
        File dir = new File("upload-dir");
        if (!dir.exists()) {
            boolean result = false;
            System.out.println("upload-dir finnes ikke i dag");

            try {
                dir.mkdir();
                result = true;
            } catch (SecurityException se) {
                //handle it
            }
            if (result) {
                System.out.println("upload-dir opprettet");
            }
        }
    }
}
