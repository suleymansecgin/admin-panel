package com.suleymansecgin.admin_panel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReactRouterController {
    
    /**
     * React Router için fallback controller.
     * Tüm frontend route'ları index.html'e yönlendirir.
     * Bu sayede sayfa yenilendiğinde (F5) React Router client-side routing yapabilir.
     */
    @GetMapping(value = {
        "/",
        "/login",
        "/register",
        "/dashboard",
        "/{path:[^\\.]*}"  // Nokta içermeyen tüm path'ler (dosya uzantısı olmayan)
    })
    public String forwardToIndex() {
        return "forward:/index.html";
    }
}

