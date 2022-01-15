package com.libumu.mubook.api;

import com.libumu.mubook.dao.faq.FaqDao;
import com.libumu.mubook.entities.Faq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Controller
@RequestMapping(path="/faq")
public class FaqController {
    
    private final FaqDao faqDao;
 
    @Autowired
    public FaqController(FaqDao faqDao) {
        this.faqDao = faqDao;
    }

    @PostMapping(path="/add")
    public String createNewFaq (Model model, @ModelAttribute Faq faq) {
        
        if(faq.getQuestion()!=null && faq.getQuestion()!="" && faq.getAnswer()!=null && faq.getAnswer()!=""){
          faqDao.addFaq(faq);
        }

        return "redirect:/faq";
    
    
    }
    @PostMapping(path="/delete")
    public String deleteFaq (Model model, @RequestParam("id") int id) {
        faqDao.deleteFaq(id);
        return "redirect:/faq";
    
    
    }
    @GetMapping(path="")
    public String getAllFaqs(Model model) {
        List<Faq> faqs=faqDao.getAllFaqs();
        model.addAttribute("faqs", faqs);
        model.addAttribute("faq", new Faq());
        
        // This returns a JSON or XML with the users
        return "faq";
    }


}
