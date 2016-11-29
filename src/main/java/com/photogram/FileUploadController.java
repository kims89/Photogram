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

/**
 * FileUploadController. Denne sørger stortsett for alle mappingene som går direkte på akkurat det med opplastinger av bilder.
 * Valgte også å legge inn /photoadmin på denne kontrolleren for at det var en mer naturlig plass med tanke på de tette knyttingene
 * som den har mot opplasting.
 */

@Controller
public class FileUploadController {

    //Her "autowire" alle repositoryene opp mot kontrolleren, det gjør at det enklere kan gjøres oppslag.
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


    //Denne legger alle fotografens bilder i en liste som presentere ut gjennom model når fotograden etterspør /photoadmin.
    //Den reverserer også listen slik at de siste bildene blir lagt ut først.
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

    //Denne tillhører opplastingsdelen, når et bilde blir etterspurt vil den vises med "localhost:8080/filer/bilde.jpg".
    @RequestMapping(value = "/files/{filename:.+}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }


    //Bilde blir opplastet ved /PAAddPhoto (post). Det postes fil, tittel (string), dato (string), beskrivelse (string).
    @RequestMapping(value = "/PAAddPhoto", method = RequestMethod.POST)
    public String handleFileUpload(@RequestParam("bild") MultipartFile file, @RequestParam("tittel") String tittel,
                                   @RequestParam("dato") String dato, @RequestParam("beskrivelse") String beskrivelse) throws Exception {


        String brukerid = "";
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        //Hener ut fotografid.
        User phgr = userRepository.findByBrukernavn(auth.getName());
        brukerid = phgr.getId();

        //Lager mappen (upload-dir) om den ikke eksisterer.
        makeFolder();

        //Oppretter en liste for å initsiere tag.
        List<String> tagList = new ArrayList<String>();

//        List<Photo> photoDList = new ArrayList<Photo>();

        //initsiere kommentarlisten.
        List<Comments> commentsList = new ArrayList<Comments>();

        //Setter opp objekt i photoklassen
        Photo p = new Photo();
        p.setFilnavn("/files/" + file.getOriginalFilename());
        p.setContentType(file.getContentType());
        p.setDato(dato);
        p.setBeskrivelse(beskrivelse);
        p.setTag(tagList);
        p.setTittel(tittel);
        p.setPhotographerID(brukerid);
        p.setKommentarer(commentsList);

        //Vi har satt en if hvis filen som lastes opp ikke er et bilde.
        if (file.getContentType().contains("image")) {
            //Vi har også en if hvis filen allerede eksisterer. Da blir det returnert feil tilbake til klientdelen.
            File y = new File("upload-dir/" + file.getOriginalFilename());
            if (!y.exists()) {
                //Filen lagres til mappe og sti til databasen.
                storageService.store(file);
                photoRepository.save(p);
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

    //Her slettes filen. Det er gitt at filen og alle spor fra databasene bilder og kommentera slettes.
    //Filen slettes også fra mappen.
    @RequestMapping(path = "/delete/{id}", method = RequestMethod.POST)
    @ResponseBody
    public void delete(@PathVariable("id") String id) {
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

    //Denne metoden oppretter en mappe
    public void makeFolder() {
        File dir = new File("upload-dir");
        if (!dir.exists()) {
            boolean result = false;
            System.out.println("upload-dir finnes ikke i dag");
            //Først forsøkes det å opprettes en mappen...
            try {
                dir.mkdir();
                result = true;
                //Om det ikke går vil det bli sendt en exception..
            } catch (SecurityException se) {
            }
            if (result) {
                System.out.println("upload-dir opprettet");
            }
        }
    }
}
