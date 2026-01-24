package com.stepin.jobopening.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Home controller for redirecting root path to Swagger UI.
 */
@Controller
public class HomeController {

    /**
     * Redirects root path to Swagger UI documentation.
     *
     * @return RedirectView to Swagger UI
     */
    @GetMapping("/")
    public RedirectView home() {
        return new RedirectView("/swagger-ui.html");
    }
}
