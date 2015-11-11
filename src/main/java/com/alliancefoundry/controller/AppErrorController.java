package com.alliancefoundry.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by Paul Bernard on 11/10/15.
 */
@RestController
public class AppErrorController implements ErrorController {

    private static final Logger log = LoggerFactory.getLogger(AppErrorController.class);

    public AppErrorController(){}


    @Autowired
    private ErrorAttributes errorAttributes;

    private final static String ERROR_PATH = "/error";


    public AppErrorController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }


    @RequestMapping(value = ERROR_PATH)
    public ErrorJson error(HttpServletRequest request, HttpServletResponse response) {
        if (response.getStatus() == 404){
            Map<String, Object> eas = getErrorAttributes(request, false);
            eas.put("message", "The resource requested was not found.");
            return new ErrorJson(response.getStatus(), eas);

        } else {
            return new ErrorJson(response.getStatus(), getErrorAttributes(request, false));
        }

    }

    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }


    private Map<String, Object> getErrorAttributes(HttpServletRequest request,
                                                   boolean includeStackTrace) {
        RequestAttributes requestAttributes = new ServletRequestAttributes(request);
        return this.errorAttributes.getErrorAttributes(requestAttributes, includeStackTrace);
    }


}
