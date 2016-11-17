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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

    @RequestMapping("/photoadmin")
    public String home(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<User> userList = new ArrayList<User>();
        for(User p : userRepository.findAll()){
            if(p.getBrukernavn() != null && p.getBrukernavn().contains(auth.getName())) {
                userList.add(p);
            }
        }
        model.addAttribute("fotograf", userList);
        String name = auth.getName(); //get logged in username
        System.out.println(name);

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

    @PostMapping("/PAAddPhoto")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, @RequestParam("tittel") String tittel) {

        String brukerid = "";
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        for(User phgr : userRepository.findAll()){
            if(phgr.getBrukernavn() != null && phgr.getBrukernavn().contains(auth.getName())) {
                brukerid = phgr.getId();

            }
        }

        File dir = new File("upload-dir");
        if (!dir.exists()) {
            boolean result = false;
            System.out.println("upload-dir finnes ikke i dag");

            try{
                dir.mkdir();
                result = true;
            }
            catch(SecurityException se){
                //handle it
            }
            if(result) {
                System.out.println("upload-dir opprettet");
            }
        }


        List<Photo> photoDList = new ArrayList<Photo>();
        Photo p = new Photo();
        System.out.println(file.getOriginalFilename());
        p.setFilnavn("/files/"+file.getOriginalFilename());
        p.setContentType(file.getContentType());
        p.setDato("?");
        p.setTag("?");
        p.setTittel(tittel);
        p.setPhotographerID(brukerid);
        photoRepository.save(p);
        storageService.store(file);

        photoRepository.save(p);

        User user = userRepository.findOne(brukerid);
        for(Photo ph : photoRepository.findAll()){
            if(ph.getPhotographerID() != null && ph.getPhotographerID().contains(brukerid)) {
                photoDList.add(ph);
            }
        }
        user.setPhotos(photoDList);
        userRepository.save(user);

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
        System.out.println(id);
        Photo p = photoRepository.findOne(id);
        photoRepository.delete(id);
        String filnavn = p.getFilnavn();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        updateUser(auth.getName());
        File file = new File("upload-dir/"+filnavn.replace("/files/",""));
        file.delete();



    }

    public void updateUser(String curUser){
        String brukerid="";
        List<Photo> photoDList = new ArrayList<Photo>();
        for(User phgr : userRepository.findAll()){
            if(phgr.getBrukernavn() != null && phgr.getBrukernavn().contains(curUser)) {
                brukerid = phgr.getId();

            }
            User user = userRepository.findOne(brukerid);
            for(Photo ph : photoRepository.findAll()){
                if(ph.getPhotographerID() != null && ph.getPhotographerID().contains(brukerid)) {
                    photoDList.add(ph);
                }
            }
            user.setPhotos(photoDList);
            userRepository.save(user);
        }
    }
}
