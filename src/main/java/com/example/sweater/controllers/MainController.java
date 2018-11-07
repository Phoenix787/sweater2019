package com.example.sweater.controllers;

import com.example.sweater.domain.Message;
import com.example.sweater.domain.User;
import com.example.sweater.repositories.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Controller
public class MainController {

    @Autowired
    private MessageRepository messageRepository;

    @Value("${upload.path}")
    private String uploadPath;

//    @Autowired
//    ServletContext servletContext;



    @GetMapping("/")
    public String greeting(/*@RequestParam(name = "name", required = false, defaultValue = "World") String name,*/
                           Map<String, Object> model) {
        /*model.put("name", name);*/
        return "greeting";
    }

    @GetMapping("/main")
    public String main(@RequestParam(required = false, defaultValue = "") String filter,
                       Model model) {
        List<Message> messages = messageRepository.findAll();

        if (filter!=null && !filter.isEmpty())
        {
            messages = messageRepository.findByTag(filter);
        } else {
            messages  = messageRepository.findAll();
        }

        model.addAttribute("messages", messages);
        model.addAttribute("filter", filter);

        return "main";
    }

    @PostMapping("/main")
    public String add(
            @AuthenticationPrincipal User user,
            @Valid Message message,
            BindingResult bindingResult,
            @RequestParam("file") MultipartFile file,
            Model model,
            WebRequest request) throws IOException {


        message.setAuthor(user);

        if (bindingResult.hasErrors()) {

            Map<String, String> errorMap = ControllerUtils.getErrors(bindingResult);

            model.mergeAttributes(errorMap);
            model.addAttribute("message", message);

        } else {

            if (file != null && !file.getOriginalFilename().isEmpty()) {

                //         String path = servletContext.getRealPath("/uploads/");

                File uploadDir = new File(uploadPath);

                if (!uploadDir.exists()) {
                    uploadDir.mkdir();
                }

                String uuid = UUID.randomUUID().toString();
                String resultFilename = uuid + "." + file.getOriginalFilename();
                file.transferTo(new File(uploadPath + "/" + resultFilename));
                message.setFilename(resultFilename);
            }

            model.addAttribute("message", null);
            messageRepository.save(message);
        }

        List<Message> messages = messageRepository.findAll();

        model.addAttribute("messages", messages);

        return "main";
    }




}
