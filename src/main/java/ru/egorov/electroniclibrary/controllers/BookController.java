package ru.egorov.electroniclibrary.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.egorov.electroniclibrary.dao.AuthorDAO;
import ru.egorov.electroniclibrary.dao.BookDAO;
import ru.egorov.electroniclibrary.dao.ClientDAO;
import ru.egorov.electroniclibrary.dao.RentDAO;
import ru.egorov.electroniclibrary.models.Book;
import ru.egorov.electroniclibrary.models.Validator.BookValidator;

@Controller
@RequestMapping("/books")
public class BookController {

    private final AuthorDAO authorDAO;
    private final ClientDAO clientDAO;
    private final BookDAO bookDAO;
    private final RentDAO rentDAO;
    private final BookValidator bookValidator;

    @Autowired
    public BookController(AuthorDAO authorDAO, ClientDAO clientDAO, BookDAO bookDAO, RentDAO rentDAO, BookValidator bookValidator) {
        this.authorDAO = authorDAO;
        this.clientDAO = clientDAO;
        this.bookDAO = bookDAO;
        this.rentDAO = rentDAO;
        this.bookValidator = bookValidator;
    }

    @GetMapping
    public String showListBooks(Model model) {
        model.addAttribute("books", bookDAO.getAll());
        return "books/index";
    }

    @GetMapping("/{id}")
    public String showInfo(@PathVariable("id") int id, Model model) {
        model.addAttribute("book", bookDAO.get(id));
        model.addAttribute("rents", rentDAO.getByBook(id));
        model.addAttribute("clients", clientDAO.getAll());
        return "books/book";
    }

    // Create

    @GetMapping("/new")
    public String showCreatePage(@ModelAttribute("book") Book book, Model model) {
        model.addAttribute("authors", authorDAO.getAll());
        return "books/new";
    }

    @PostMapping("/new")
    public String createBook(@ModelAttribute("book") @Valid Book book,
                             BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("authors", authorDAO.getAll());
            return "books/new";
        }
        bookValidator.validate(book, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("authors", authorDAO.getAll());
            return "books/new";
        }
        bookDAO.add(book);
        return "redirect:/books";
    }

    // Update

    @GetMapping("/{id}/edit")
    public String showEditPage(@PathVariable("id") int id, Model model) {
        model.addAttribute("book", bookDAO.get(id));
        model.addAttribute("authors", authorDAO.getAll());
        return "books/edit";
    }

    @PatchMapping("/{id}")
    public String updateBook(@PathVariable("id") int id,
                             @ModelAttribute("book") @Valid Book book,
                             BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("authors", authorDAO.getAll());
            return "books/edit";
        }
        bookValidator.validate(book, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("authors", authorDAO.getAll());
            return "books/edit";
        }
        bookDAO.update(book);
        return "redirect:/books/" + id;
    }

    // Delete

    @DeleteMapping("/{id}")
    public String deleteBook(@PathVariable("id") int id) {
        bookDAO.delete(id);
        return "redirect:/books";
    }


}
