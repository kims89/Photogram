package com.photogram;

import com.photogram.POJO.Comments;
import com.photogram.POJO.Photo;
import com.photogram.POJO.Search;
import com.photogram.POJO.User;
import com.photogram.Repository.CommentsRepository;
import com.photogram.Repository.PhotoRepository;
import com.photogram.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class PhotogramController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PhotoRepository photoRepository;

    @Autowired
    CommentsRepository commentsRepository;


    //Ved index forespørsel blir alle bildene fra alle fotografene presentert.
    //Hele listen blir snudd opp ned slik at siste opplastede kommer først.
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String homeUser(Model model) {
        List<Photo> pho = new ArrayList<Photo>();
        pho.addAll(photoRepository.findAll());
        Collections.reverse(pho);
        model.addAttribute("photo", pho);

        return "photouser";
    }


    //Denne requestmappingen gir tilgang til loginsiden.
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login() {
        return "/login";
    }

    //Denne requestmappingen gir tilgang til en spesialtiplasset 403 side.
    @RequestMapping(value = "/403", method = RequestMethod.GET)
    public String firenulltre() {
        return "/403";
    }

    //Ved endring bildet får brukeren muligheten til å endre tittel, beskrivelse og dato. Vi mener at
    //fotografen ikke skalha muligheten til å endre bildet også, for da blir objektet helt annet enn det
    //egentlig var ment til.

    //Endring av bilder krever: id, tittel og beskrivelse.
    @RequestMapping(value = "/PAChangePhoto", method = RequestMethod.POST)
    public String handleChange(@RequestParam("iid") String id, @RequestParam("itittel") String tittel, @RequestParam("ibeskrivelse") String beskrivelse, @RequestParam("idato") String dato) {
        Photo p = photoRepository.findOne(id);
        p.setTittel(tittel);
        p.setBeskrivelse(beskrivelse);
        p.setDato(dato);
        photoRepository.save(p);

        //Den returner alltid photoadminsiden.
        return "redirect:photoadmin";
    }


    //Her gis det mulighet til å opprette en bruker. Brukerne blir alltid satt på rolle som ROLE_USER_
    //Senere blir brukeren lagret i databasen.
    //Denne krever: fornavn, brukernavn, etternavn og passord.
    @RequestMapping(value = "/NewUser", method = RequestMethod.POST)
    public String nyBruker(@RequestParam(value = "fornavn") String fornavn, @RequestParam(value = "etternavn") String etternavn,
                           @RequestParam(value = "brukernavn") String brukernavn, @RequestParam("passord") String passord) {
        User user;
        if (brukernavn != "") {
            if (userRepository.findByBrukernavn(brukernavn) == null) {
                user = new User(fornavn, etternavn, brukernavn, passord, "ROLE_USER");
                userRepository.save(user);

                System.out.println("Ny bruker opprettet med brukernavn: " + brukernavn + " og passord: " + passord);
            }
        } else {
            System.out.println("Brukerfelt må ha en verdi");
        }

        return "redirect:login";
    }

    //Ved innlegg av kommentar må brukeren sende navn (oppfylles av web security) bildeid (oppfylles av thymeleaf).
    //kommentar (oppfylles av brukeren) og rolle (oppfylles av thymeleaf).
    @RequestMapping(value = "/addComments", method = RequestMethod.POST)
    public String addcom(@RequestParam(value = "navn") String navn, @RequestParam(value = "bildeid") String bildeid,
                         @RequestParam(value = "kommentar") String kommentar, @RequestParam("rolle") String rolle) {

        //Det settes en verdi uavhengig av om brukeren er fotograf eller ikke. Om han fotograf får brukeren
        //en grå flik ved siden av navnet hvor det står bruker, hvis ikke får han en blå flik hvor det står fotograf.
        String FargeRolle = "";
        if (rolle.equals("Bruker")) {
            FargeRolle = "label label-default";
        } else if (rolle.equals("Fotograf")) {
            FargeRolle = "label label-primary";
        }

        //Når kommentaren legges til blir listen hentet ut fra fotoobjektet og lagt til kommentar. deretter lagres det
        //til mongodb.
        List<Comments> commentsList = new ArrayList<Comments>();
        Comments c = new Comments(navn, kommentar, bildeid, rolle, FargeRolle);
        commentsRepository.save(c);
        Photo p = photoRepository.findOne(bildeid);
        if (p.getKommentarer() != null) {
            commentsList.addAll(p.getKommentarer());
        }
        commentsList.add(c);
        p.setKommentarer(commentsList);
        photoRepository.save(p);
        return "photouser";
    }

    //Ved /sok forespørsel blir det returnert en liste med alle objektene (fotograf, tag og bildetittel)
    //som skal vises ved søk. Ved søk på bilde blir brukeren pekt mot /photo/*bildeid*, /tag/*tag*
    // /photographer/*fotografid*. Alt dette returneres til JSON-liste.
    @RequestMapping(value = "/sok", method = RequestMethod.GET)
    public
    @ResponseBody
    List getSearchInJSON() {

        List<Search> sokList = new ArrayList<>();
        List<String> tagglist = new ArrayList<String>();
        for (Photo ph : photoRepository.findAll()) {
            sokList.add(new Search(ph.getTittel() + " (Bilde)", "/photo/" + ph.getId()));
        }

        for (Photo pht : photoRepository.findAll()) {
            tagglist.addAll(pht.getTag());
        }
        Set<String> hs = new HashSet<>();
        hs.addAll(tagglist);
        tagglist.clear();
        tagglist.addAll(hs);

        for (String s : tagglist) {
            sokList.add(new Search("#" + s, "/tag/" + s));

        }

        for (User us : userRepository.findAll()) {
            if (us.getRolle() != null && us.getRolle().contains("ROLE_ADMIN")) {
                sokList.add(new Search(us.getBrukernavn() + " (Fotograf)", "/photographer/" + us.getId()));
            }
        }

        return sokList;

    }

    //ved oppslag av tag vil alle bildene med samme tagg bli listet gjennom photouser-siden.
    @RequestMapping(value = "tag/{id}", method = RequestMethod.GET)
    public String tagghome(Model model, @PathVariable String id) {
        List<Photo> photoList = new ArrayList<Photo>();
        for (Photo p : photoRepository.findAll()) {
            if (p.getTag().contains(id)) {
                photoList.add(p);
            }
        }
        model.addAttribute("photo", photoList);


        return "photouser";
    }

    //Ved oppslag av photo vil det bilde med den iden som er spesifisert bli listen på photouser-siden.
    @RequestMapping(value = "photo/{id}", method = RequestMethod.GET)
    public String phohome(Model model, @PathVariable String id) {
        List<Photo> photoList = new ArrayList<Photo>();
        Photo ph = photoRepository.findOne(id);
        photoList.add(ph);
        model.addAttribute("photo", photoList);

        return "photouser";
    }


    //Ved søk på fotograf vil bare den fotografen som søkes bli listet i photouser-siden
    @RequestMapping(value = "photographer/{id}", method = RequestMethod.GET)
    public String photographhome(Model model, @PathVariable String id) {
        List<Photo> photoList = new ArrayList<Photo>();
        for (Photo ph : photoRepository.findByphotographerID(id)) {
            photoList.add(ph);
        }
        model.addAttribute("photo", photoList);

        return "photouser";
    }

    //Når en tag blir satt vil iden fra bildet og den evt taggen bli sendt. Taggen vil deretter blir lagt under i
    // en stringliste under bildet, og lagret i databasen.
    @PostMapping("/setTag")
    public String handleSetTag(@RequestParam("id") String id, @RequestParam("tag") String tag) {
        List<String> bufferTag;
        Photo p = photoRepository.findOne(id);
        if (!p.getTag().contains(tag)) {
            bufferTag = p.getTag();
            p.setTag(null);
            bufferTag.add(tag);
            p.setTag(bufferTag);
            photoRepository.save(p);
        }

        return "redirect:photoadmin";
    }

    //Når en tagg slettes fra bildet vil bildet bli tatt ut av databasen, fjernet taggen fra listen og deretter
    //lagt inn i databasen igjen. Dette er tidbesparende og veldig skalerbart.
    @PostMapping("/deleteTag")
    public String handleDeleteTag(@RequestParam("id") String id, @RequestParam("tag") String tag) {
        List<String> bufferTag;
        Photo p = photoRepository.findOne(id);
        if (p.getTag().contains(tag)) {
            bufferTag = p.getTag();
            p.setTag(null);
            bufferTag.remove(tag);
            p.setTag(bufferTag);
            photoRepository.save(p);
        }


        return "redirect:photoadmin";
    }


    //sett rolle gis brukeren fotografrettigheter, Når brukeren ber om tilgang får han endret rollen fra ROLE_USER til
    //ROLE_ADMIN. Deretter blir brukeren logget av og lagt ved en beskjed i model hvor han/hun får beskjed om å logge
    //inn igjen for å fullføre prosessen med å bli fotograf.
    @RequestMapping(value = "/SetRolePhotographer", method = RequestMethod.GET)
    public String messages(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User p = userRepository.findByBrukernavn(auth.getName());
        if (p.getRolle().equals("ROLE_USER")) {
            //her settes rollen til ROLE_ADMIN, og deretter lagres til databasen
            p.setRolle("ROLE_ADMIN");
            userRepository.save(p);
            model.addAttribute("Beskjed", "Logg inn på nytt for å bli en fotograf.");

        } else {

            model.addAttribute("Beskjed", "Du er allerede fotograf... Du trenger ikke utvidede rettigheter for å legge inn bilder.");

        }
        //Her blir brukeren logget ut
        SecurityContextHolder.getContext().setAuthentication(null);

        return "login";
    }


}
