package com.photogram;

import com.photogram.Repository.CommentsRepository;
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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Fredrik on 15.11.2016.
 */
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

    @RequestMapping("/photoadmin")
    public String homeAdmin(Model model) {

        String brukerid = "";
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User u = userRepository.findByBrukernavn(auth.getName());
        List<Photo> photoList = new ArrayList<Photo>();
        for(Photo ph : photoRepository.findAll()){
            if(ph.getPhotographerID() != null && ph.getPhotographerID().contains(u.getId())) {
                photoList.add(ph);
            }
        }
        Collections.reverse(photoList);
        model.addAttribute("photo", photoList);

        return "photoadmin";
    }

    @RequestMapping("/photouser")
    public String homeUser(Model model) {

        Photo p = photoRepository.findOne("58356b1136b59d09f42edbae");
        Comments c = new Comments("Kim","Arne og Fredrik skal gjøre det og det og det! æ bestemme","583550f936b59d12582c32d6");
        Comments d = new Comments("Arne","Hehehe","58356b1136b59d09f42edbae");
        Comments e = new Comments("Fredrik","Nope!","58356b1136b59d09f42edbae");


        List<Comments> kom = new ArrayList<Comments>();
        List<Photo> pho = new ArrayList<Photo>();
        kom.add(c);
        kom.add(d);
        kom.add(e);
        p.setKommentarer(kom);
        commentsrepository.save(c);
        photoRepository.save(p);
        pho.addAll(photoRepository.findAll());
        Collections.reverse(pho);
        model.addAttribute("photo", pho);

        return "photouser";
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
    public String handleFileUpload(@RequestParam("bild") MultipartFile file, @RequestParam("tittel") String tittel,
                                   @RequestParam("dato") String dato, @RequestParam ("beskrivelse") String beskrivelse) {

        String brukerid = "";
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        for(User phgr : userRepository.findAll()){
            if(phgr.getBrukernavn() != null && phgr.getBrukernavn().contains(auth.getName())) {
                brukerid = phgr.getId();

            }
        }

        makeFolder();
        List<String> tagList = new ArrayList<String>();
        List<Photo> photoDList = new ArrayList<Photo>();
        Photo p = new Photo();
        System.out.println(file.getOriginalFilename());
        p.setFilnavn("/files/"+file.getOriginalFilename());
        p.setContentType(file.getContentType());
        p.setDato(dato);
        p.setBeskrivelse(beskrivelse);
        p.setTag(tagList);
        p.setTittel(tittel);
        p.setPhotographerID(brukerid);
        if(file.getContentType().contains("image")){
            photoRepository.save(p);
            storageService.store(file);

            photoRepository.save(p);
            User user = userRepository.findOne(brukerid);
            for(Photo ph : photoRepository.findAll()){
                if(ph.getPhotographerID() != null && ph.getPhotographerID().contains(brukerid)) {
                    photoDList.add(ph);
                }
            }
        }


        return "redirect:photoadmin";
    }



    @PostMapping("/PAChangePhoto")
    public String handleChange(@RequestParam("iid") String id,@RequestParam("itittel") String tittel,@RequestParam("ibeskrivelse") String beskrivelse, @RequestParam("idato") String dato) {
        Photo p = photoRepository.findOne(id);
        p.setTittel(tittel);
        p.setBeskrivelse(beskrivelse);
        p.setDato(dato);
        photoRepository.save(p);

        return "redirect:photoadmin";
    }

    @PostMapping("/setTag")
    public String handleSetTag(@RequestParam("id") String id,@RequestParam("tag") String tag) {
        List<String> bufferTag;
        Photo p = photoRepository.findOne(id);
        if(!p.getTag().contains(tag)) {
            bufferTag=p.getTag();
            p.setTag(null);
            bufferTag.add(tag);
            p.setTag(bufferTag);
            photoRepository.save(p);
            }

        System.out.println(id+tag);

        return "redirect:photoadmin";
    }

    @PostMapping("/deleteTag")
    public String handleDeleteTag(@RequestParam("id") String id,@RequestParam("tag") String tag) {
        List<String> bufferTag;
        Photo p = photoRepository.findOne(id);
        if(p.getTag().contains(tag)) {
            bufferTag=p.getTag();
            p.setTag(null);
            bufferTag.remove(tag);
            p.setTag(bufferTag);
            photoRepository.save(p);
        }


        return "redirect:photoadmin";
    }


    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

    @RequestMapping(path = "/delete/{id}", method = RequestMethod.POST)
    public @ResponseBody void delete(@PathVariable("id") String id){
        System.out.println(id);
        Photo p = photoRepository.findOne(id);
        photoRepository.delete(id);
        String filnavn = p.getFilnavn();

        File file = new File("upload-dir/"+filnavn.replace("/files/",""));
        file.delete();



    }

    public void makeFolder(){
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
    }
}
