package com.nnk.springboot.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorController {

    /**
     * Handles HTTP 403 Forbidden errors by returning the corresponding error page.
     *
     * @return the name of the view for the 403 error page
     */
    @GetMapping("/403")
    public String error403() {
        return "403";
    }

}