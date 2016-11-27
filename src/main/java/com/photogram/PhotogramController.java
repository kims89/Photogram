package com.photogram;

        import com.photogram.Repository.CommentsRepository;
        import com.photogram.Repository.PhotoRepository;
        import com.photogram.Repository.UserRepository;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.security.core.Authentication;
        import org.springframework.security.core.context.SecurityContextHolder;
        import org.springframework.stereotype.Controller;
        import org.springframework.ui.Model;
        import org.springframework.web.bind.annotation.*;

        import javax.servlet.http.HttpServletRequest;
        import javax.servlet.http.HttpSession;
        import java.util.ArrayList;
        import java.util.HashSet;
        import java.util.List;
        import java.util.Set;

@Controller
public class PhotogramController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PhotoRepository photoRepository;

    @Autowired
    CommentsRepository commentsRepository;


    //Denne requestmappingen gir tilgang til loginsiden.
    @RequestMapping("/login")
    public String login(){
        return "/login";
    }


    //metode som lar lærer opprette brukere i mongoDB til innlogging i handleliste
    @RequestMapping(value="/NewUser", method = RequestMethod.POST)
    public String nyBruker(@RequestParam(value = "fornavn") String fornavn, @RequestParam(value = "etternavn") String etternavn,
                           @RequestParam(value = "brukernavn") String brukernavn, @RequestParam("passord") String passord){
        User user;
        if (brukernavn != "") {
            if (userRepository.findByBrukernavn(brukernavn) == null){
                user = new User(fornavn, etternavn, brukernavn, passord, "ROLE_USER");
                userRepository.save(user);

                System.out.println("Ny bruker opprettet med brukernavn: "+brukernavn+" og passord: "+passord);
            }
        }
        else {
            System.out.println("Brukerfelt må ha en verdi");
        }

        return "redirect:login";
    }

    //metode som lar lærer opprette brukere i mongoDB til innlogging i handleliste
    @RequestMapping(value="/addComments", method = RequestMethod.POST)
    public String addcom(@RequestParam(value = "navn") String navn, @RequestParam(value = "bildeid") String bildeid,
                         @RequestParam(value = "kommentar") String kommentar, @RequestParam ("rolle") String rolle){

        String FargeRolle="";
        if(rolle.equals("Bruker")){
            FargeRolle = "label label-default";
        }
        else if (rolle.equals("Fotograf")){
            FargeRolle = "label label-primary";
        }


        List<Comments> commentsList = new ArrayList<Comments>();
        Comments c = new Comments(navn,kommentar,bildeid,rolle,FargeRolle);
        commentsRepository.save(c);
        Photo p=photoRepository.findOne(bildeid);
        if(p.getKommentarer()!=null){
            commentsList.addAll(p.getKommentarer());
        }
        commentsList.add(c);
        p.setKommentarer(commentsList);
        photoRepository.save(p);
        System.out.println(c.getNavn()+" "+c.getKommentar()+" "+c.getPhotoID());
        return "photouser";
    }

    @RequestMapping(value="/sok", method = RequestMethod.GET)
    public @ResponseBody List getSearchInJSON() {

        List<Search> sokList = new ArrayList<>();
        List<String> tagglist = new ArrayList<String>();
        for(Photo ph : photoRepository.findAll()){
            sokList.add(new Search(ph.getTittel()+" (Bilde)","/photo/"+ph.getId()));
        }

        for(Photo pht : photoRepository.findAll()){
            tagglist.addAll(pht.getTag());
        }
        Set<String> hs = new HashSet<>();
        hs.addAll(tagglist);
        tagglist.clear();
        tagglist.addAll(hs);

        for(String s: tagglist){
            sokList.add(new Search("#"+s,"/tag/"+s));

        }

        for(User us : userRepository.findAll()){
            if(us.getRolle() != null && us.getRolle().contains("ROLE_ADMIN")) {
                sokList.add(new Search(us.getBrukernavn()+" (Fotograf)","/photographer/"+us.getId()));
            }
        }

        return sokList;

    }

    @RequestMapping(value="tag/{id}",method = RequestMethod.GET)
    public String tagghome( Model model,@PathVariable String id) {
        List<Photo> photoList = new ArrayList<Photo>();
        for (Photo p : photoRepository.findAll()){
            if(p.getTag().contains(id)){
                photoList.add(p);
            }
        }
        model.addAttribute("photo", photoList);


        return "photouser";
    }

    @RequestMapping(value="photo/{id}",method = RequestMethod.GET)
    public String phohome( Model model,@PathVariable String id) {
        List<Photo> photoList = new ArrayList<Photo>();
        Photo ph=photoRepository.findOne(id);
        photoList.add(ph);
        model.addAttribute("photo", photoList);

        return "photouser";
    }


    @RequestMapping(value="photographer/{id}", method = RequestMethod.GET)
    public String photographhome( Model model,@PathVariable String id) {
        List<Photo> photoList = new ArrayList<Photo>();
        for(Photo ph : photoRepository.findByphotographerID(id)){
            photoList.add(ph);
        }
        model.addAttribute("photo", photoList);

        return "photouser";
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

    @RequestMapping(value = "/SetRolePhotographer", method = RequestMethod.GET)
    public String messages(HttpServletRequest request, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User p = userRepository.findByBrukernavn(auth.getName());
        p.setRolle("ROLE_ADMIN");
        userRepository.save(p);
        HttpSession httpSession = request.getSession();
        httpSession.invalidate();
        model.addAttribute("Beskjed", "Logg inn på nytt for å bli en fotograf.");
        return "login";
    }



}
