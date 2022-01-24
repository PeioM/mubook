package com.libumu.mubook.api;

import com.libumu.mubook.dao.comment.CommentDao;
import com.libumu.mubook.dao.incidence.IncidenceDao;
import com.libumu.mubook.dao.user.UserDao;
import com.libumu.mubook.entities.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigInteger;
import java.util.*;


import com.libumu.mubook.dao.item.ItemDao;
import com.libumu.mubook.dao.itemModel.ItemModelDao;
import com.libumu.mubook.dao.itemType.ItemTypeDao;
import com.libumu.mubook.dao.reservation.ReservationDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(path="/reservations")
public class ReservationController {

    private final ReservationDao reservationDao;
    private final ItemModelDao itemModelDao;
    private final ItemDao itemDao;
    private final UserDao userDao;
    private final IncidenceDao incidenceDao;
    private final CommentDao commentDao;
    @Autowired
    public ReservationController(ReservationDao reservationDao, ItemModelDao itemModelDao, ItemDao itemDao, UserDao userDao, IncidenceDao incidenceDao, CommentDao commentDao) {
        this.reservationDao = reservationDao;
        this.itemModelDao = itemModelDao;
        this.itemDao = itemDao;
        this.userDao = userDao;
        this.incidenceDao = incidenceDao;
        this.commentDao = commentDao;
    }

    @GetMapping(path="/all")
    public String getActiveReservations(Model model){
        Map<ItemType,Set<ItemModel>> filters = new TreeMap<>();
        List<ItemModel> itemModelList = itemModelDao.getAllItemModels();
        Set<ItemModel> itemModels = new TreeSet<>(itemModelList);

        //Create map for ItemType and Models
        for(ItemModel im : itemModels){
            ItemType it = im.getItemType();
            Set<ItemModel> values;
            if(filters.containsKey(it)){
                values = filters.get(it);
                values.add(im);
            }
            else{
                values = new TreeSet<>();
                values.add(im);
                filters.put(it, values);
            }
        }
        model.addAttribute("filters", filters);

        return "myReservations";
    }


    @GetMapping(path="/{id}/view")
    public ModelAndView getReservation(@PathVariable("id") String reservationId, Model model){
        Reservation reservation = reservationDao.getReservation(Long.parseLong(reservationId));

        model.addAttribute("reserve", reservation);

        return new ModelAndView("reservation", new ModelMap(model));
    }

    @GetMapping(path="/{itemModelId}/offer")
    public ModelAndView makeReservationOffer(Model model,
                                            @PathVariable("itemModelId") String itemModelStr){

        Long itemModelId = Long.parseLong(itemModelStr);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userDao.getUserByUsername(username);
        ItemModel itemModel = itemModelDao.getItemModel(itemModelId);
        Item item = null;
        Date initDate = null;
        String error = "";
        String returnStr = "";

        List<Long> itemsWithoutR = reservationDao.getItemsWithoutReservation(itemModel.getItemModelId());
        List<Object[]> result = reservationDao.getFirstReservationDate(itemModel.getItemModelId());
        if(itemsWithoutR.size() != 0){
            item = itemDao.getItem(itemsWithoutR.get(0));
            initDate = new Date();
        }else if(result.size() != 0) {
            initDate = (Date) result.get(0)[0];
            BigInteger itemId = (BigInteger) result.get(0)[1];
            item = itemDao.getItem(itemId.longValue());
        }else{
            error = "No está disponible este modelo";
        }

        if(error.length() == 0){
            int numIncidence = incidenceDao.countSumIncidenceByUserId(user.getUserId());;
            if(numIncidence >= 3){
                error = "Tienes mas de 3 incidencias acumuladas actualmente";
            }
        }

        if(error.length() == 0){
            Date actualDate = new Date();
            if(initDate.compareTo(actualDate) < 0){
                initDate = actualDate;
            }

            Calendar c = Calendar.getInstance();
            c.setTime(initDate);
            c.add(Calendar.DATE, 10);
            Date endDate = c.getTime();

            Reservation reservation = new Reservation();

            reservation.setItem(item);
            reservation.setInitDate(new java.sql.Date(initDate.getTime()));
            reservation.setEndDate(new java.sql.Date(endDate.getTime()));
            reservation.setUser(user);

            model.addAttribute("reserve", reservation);
            model.addAttribute("offer", "offer");
            returnStr = "reservation";
        }else{
            List<Comment> comments = commentDao.getAllComentsByItemModelId(itemModelId);
            model.addAttribute("error", error);
            model.addAttribute("itemModel", itemModel);
            model.addAttribute("username", user.getUsername());
            model.addAttribute("commentItem", new Comment());
            model.addAttribute("comments", comments);

            returnStr = "item";
        }

        return new ModelAndView(returnStr, new ModelMap(model));
    }

    @GetMapping(path="/{itemModelId}/create")
    public ModelAndView makeReservationWorker(Model model,
                                            @PathVariable("itemModelId") String itemModelStr){

        Long itemModelId = Long.parseLong(itemModelStr);
        
        ItemModel itemModel = itemModelDao.getItemModel(itemModelId);

        List<Object[]> result = reservationDao.getFirstReservationDate(itemModel.getItemModelId());
        Date initDate = (Date) result.get(0)[0];
        BigInteger itemId = (BigInteger) result.get(0)[1];
        Item item = itemDao.getItem(itemId.longValue());

        Date actualDate = new Date();
        if(initDate.compareTo(actualDate) < 0){
            initDate = actualDate;
        }

        Calendar c = Calendar.getInstance();
        c.setTime(initDate);

        Reservation reservation = new Reservation();

        reservation.setItem(item);
        reservation.setInitDate(new java.sql.Date(initDate.getTime()));

        model.addAttribute("reserve", reservation);

        return new ModelAndView("reservation", new ModelMap(model));
    }

    @PostMapping(path="/add")
    public String addReservation(Model model,
                                    @ModelAttribute Reservation reservation){
        reservation.setItem(itemDao.getItem(reservation.getItem().getItemId()));                                 
        reservation.setUser(userDao.getUser(reservation.getUser().getUserId()));                             
        reservationDao.addReservation(reservation);

        return "redirect:/index";
    }

    @PostMapping(path="/edit")
    public String editReservation(Reservation reservation, Model model){
        reservationDao.editReservation(reservation);

        return "redirect:/index";
    }

    @PostMapping(path="/delete")
    public String deleteReservation(@RequestParam("id") long reservationId){
        reservationDao.deleteReservation(reservationId);

        return "redirect:/reservations/list?itemModelName=&active=true";
    }

}
