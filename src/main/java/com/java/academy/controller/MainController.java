package com.java.academy.controller;

import com.java.academy.logger.Log;
import com.java.academy.model.Book;
import com.java.academy.service.BookService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;


@Controller
@RequestMapping("/robot")
public class MainController {
	
	@Autowired
	BookService bookService;

	private static @Log Logger LOG;

	@RequestMapping()
	public String welcome(Model model) {
		model.addAttribute("start", "Robot application!");
		model.addAttribute("content", "Team: Paweł S., Artur, Mateusz B.");


		LOG.info("Welcome Logger!");
		return "start";
	}
	
	@RequestMapping("/books")
	public String books(Model model) {
		model.addAttribute("start", "Robot application!");
		model.addAttribute("content", "Team: Paweł S., Artur, Mateusz B.");
		
		model.addAttribute("books", bookService.getAllBooks());


		return "books";
	}
	
	@RequestMapping("/addBooks")
	public String addBooks(Model model) {
		model.addAttribute("start", "Robot application!");
		model.addAttribute("content", "Team: Paweł S., Artur, Mateusz B.");
		
		Book potop = new Book("Potop", "Henryk Sienkiewicz", "history novel", "-25%", new BigDecimal(32));
		Book lalka = new Book("Lalka", "Bolesław Prus", "novel", "-25%", new BigDecimal(25));
		Book krewElfow = new Book("Krew Elfów", "Andrzej Sapkowski", "fantasy", "-15%", new BigDecimal(35));
		
		bookService.addBook(potop);
		bookService.addBook(lalka);
		bookService.addBook(krewElfow);
		
		return "redirect:/robot/books";
	}
}
