package com.libumu.mubook.dao.faq;

import com.libumu.mubook.entities.Faq;

import java.util.List;

public interface FaqDao {

    public List<Faq> getAllFaqs();
    public Faq getFaq(int id);
    public void editFaq(Faq faq);
    public void deleteFaq(int id);
    public void deleteFaq(Faq faq);
    public void addFaq(Faq faq);

}
