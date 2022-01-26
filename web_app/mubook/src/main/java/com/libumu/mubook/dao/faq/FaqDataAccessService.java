package com.libumu.mubook.dao.faq;

import com.libumu.mubook.entities.Faq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FaqDataAccessService implements FaqDao {

    @Autowired
    private FaqRepository repository;

    @Override
    public List<Faq> getAllFaqs() {
        return (List<Faq>) repository.findAll();
    }

    @Override
    public Faq getFaq(int id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public void editFaq(Faq faq) {
        repository.save(faq);
    }

    @Override
    public void deleteFaq(int id) {
        repository.deleteById(id);
    }

    @Override
    public void deleteFaq(Faq faq) {
        repository.delete(faq);
    }

    @Override
    public void addFaq(Faq faq) {
        repository.save(faq);
    }

    @Override
    public Faq findByQuestion(String question) {
        return repository.findByQuestion(question);
    }
}
