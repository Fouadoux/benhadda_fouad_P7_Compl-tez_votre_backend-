package com.nnk.springboot.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController
{
	/**
	 * Displays the home page.
	 *
	 * @return the name of the home view
	 */
	@RequestMapping("/")
	public String home()
	{
		return "home";
	}

}
