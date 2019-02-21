package com.lnson.signature.demo.web.controller;

import com.lnson.signature.demo.commons.Certificate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "home")
public class HomeController {

    private final static Logger logger = LogManager.getLogger(HomeController.class);

    @RequestMapping(value = "/index.do", method = {RequestMethod.GET})
    public ModelAndView indexAction(HttpServletRequest request, HttpServletResponse response) {
        logger.debug(request);
        logger.debug(response);
        return new ModelAndView("index");
    }

}
