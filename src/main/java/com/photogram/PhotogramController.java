package com.photogram;

        import com.photogram.Repository.PhotoRepository;
        import com.photogram.Repository.PhotographerRepository;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.security.core.Authentication;
        import org.springframework.security.core.context.SecurityContextHolder;
        import org.springframework.stereotype.Controller;
        import org.springframework.ui.Model;
        import org.springframework.ui.ModelMap;
        import org.springframework.web.bind.annotation.*;

        import java.util.ArrayList;
        import java.util.List;
@Controller
public class PhotogramController {

    @Autowired
    PhotographerRepository photographerRepository;

    @Autowired
    PhotoRepository photoRepository;


    @RequestMapping("/photoadmin")
    public String home(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<Photographer> photographerList = new ArrayList<Photographer>();
        for(Photographer p : photographerRepository.findAll()){
            if(p.getBrukernavn() != null && p.getBrukernavn().contains(auth.getName())) {
                photographerList.add(p);
            }
        }
        model.addAttribute("fotograf", photographerList);
        String name = auth.getName(); //get logged in username
        System.out.println(name);

        return "photoadmin";
    }

    @RequestMapping(value = "/PAAddPhoto", method = RequestMethod.POST)
    public String leggTilVare(@RequestParam("filnavn") String filnavn) {
        String brukerid = "";
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        for(Photographer phgr : photographerRepository.findAll()){
            if(phgr.getBrukernavn() != null && phgr.getBrukernavn().contains(auth.getName())) {
                brukerid = phgr.getId();
            }
        }

        List<Photo> photoDList = new ArrayList<Photo>();
        Photo bilder = new Photo(filnavn,filnavn,filnavn,filnavn,filnavn);
        bilder.setPhotographerID(brukerid);
        photoRepository.save(bilder);
        Photographer p = photographerRepository.findOne(brukerid);
        p.setPhotos(null);
        for(Photo ph : photoRepository.findAll()){
            if(ph.getPhotographerID() != null && ph.getPhotographerID().contains(brukerid)) {
                photoDList.add(ph);
            }
        }
        p.setPhotos(photoDList);
        photographerRepository.save(p);
        return "redirect:photoadmin";
    }


    @RequestMapping(value="/opprettBilde", method=RequestMethod.GET)
    @ResponseBody
    public String foob() {
        List<Photo> photoDList = new ArrayList<Photo>();
        Photo bilder = new Photo("1","29.03.2013","Vardo","2","2");
        photoRepository.save(bilder);
        Photographer p = photographerRepository.findOne("5825a76336b59d0ef09da746");
        p.setPhotos(null);
        for(Photo ph : photoRepository.findAll()){
            if(ph.getPhotographerID() != null && ph.getPhotographerID().contains("5825a76336b59d0ef09da746")) {
                photoDList.add(ph);
            }
        }
        p.setPhotos(photoDList);

        photographerRepository.save(p);

        return "Response!";
        }




    @RequestMapping(path = "/photographer", method = RequestMethod.GET)
    public
    @ResponseBody
    List<Photographer> getPhotographerJson() {
        System.out.println("Spør etter JSON");
        return photographerRepository.findAll();
    }

    @RequestMapping(path = "/photo", method = RequestMethod.GET)
    public
    @ResponseBody
    List<Photo> getPhotoJson() {
        System.out.println("Spør etter JSON");
        return photoRepository.findAll();
    }

    //Denne requestmappingen gir tilgang til loginsiden.
    @RequestMapping("/login")
    public String login(){
        return "login";
    }

    //metode som lar lærer opprette brukere i mongoDB til innlogging i handleliste
    @RequestMapping(value="/NewPhotographer", method = RequestMethod.POST)
    public String nyBruker(@RequestParam(value = "fornavn") String fornavn, @RequestParam(value = "etternavn") String etternavn,
                           @RequestParam(value = "brukernavn") String brukernavn, @RequestParam("passord") String passord){
        Photographer photographer;
        if (brukernavn != "") {
            photographer = new Photographer(fornavn, etternavn, brukernavn, passord);
            photographerRepository.save(photographer);

            System.out.println("Ny bruker opprettet med brukernavn: "+brukernavn+" og passord: "+passord);
        }
        else {
            System.out.println("Brukerfelt må ha en verdi");
        }

        return "redirect:login";
    }

}