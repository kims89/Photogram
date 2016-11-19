package com.photogram;

        import com.photogram.Repository.PhotoRepository;
        import com.photogram.Repository.UserRepository;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.security.core.Authentication;
        import org.springframework.security.core.context.SecurityContextHolder;
        import org.springframework.stereotype.Controller;
        import org.springframework.ui.Model;
        import org.springframework.web.bind.annotation.*;

        import java.util.ArrayList;
        import java.util.List;
@Controller
public class PhotogramController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PhotoRepository photoRepository;



    @RequestMapping(path = "/user", method = RequestMethod.GET)
    public
    @ResponseBody
    List<User> getPhotographerJson() {
        System.out.println("Spør etter JSON");
        return userRepository.findAll();
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
        User user;
        if (brukernavn != "") {
            if (userRepository.findByBrukernavn(brukernavn) == null){
                user = new User(fornavn, etternavn, brukernavn, passord, "ROLE_ADMIN");
                userRepository.save(user);

                System.out.println("Ny bruker opprettet med brukernavn: "+brukernavn+" og passord: "+passord);
            }
        }
        else {
            System.out.println("Brukerfelt må ha en verdi");
        }

        return "redirect:login";
    }

    // TEST
    // --------------------------------------------------------->

    @RequestMapping(value="/sok", method = RequestMethod.GET)
    public @ResponseBody List getSearchInJSON() {
        List<Search> personLiswt = new ArrayList<>();
        for(Photo ph : photoRepository.findAll()){
            personLiswt.add(new Search(ph.getTittel(),"/photo/"+ph.getId()));
        }

        for(User us : userRepository.findAll()){
            if(us.getRolle() != null && us.getRolle().contains("ROLE_ADMIN")) {
                personLiswt.add(new Search(us.getBrukernavn(),"/photographer/"+us.getId()));
            }
        }

        return personLiswt;

    }

    @RequestMapping(value="photo/{id}", method = RequestMethod.GET)
    public String phohome( Model model,@PathVariable String id) {
        List<Photo> photoList = new ArrayList<Photo>();
        Photo ph=photoRepository.findOne(id);
        photoList.add(ph);
        model.addAttribute("photo", photoList);

        return "photoadmin";
    }


    @RequestMapping(value="photographer/{id}", method = RequestMethod.GET)
    public String photographhome( Model model,@PathVariable String id) {
        List<Photo> photoList = new ArrayList<Photo>();
        for(Photo ph : photoRepository.findByphotographerID(id)){
            photoList.add(ph);
        }
        model.addAttribute("photo", photoList);

        return "photoadmin";
    }

// --------------------------------------------------------->
//Endre rolle
//        User p = userRepository.findOne("5829f946d7a15f7b50d28245");
//        p.setRolle("ROLE_ADMIN");
//        userRepository.save(p);

}
