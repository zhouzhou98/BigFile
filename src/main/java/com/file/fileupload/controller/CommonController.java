package com.file.fileupload.controller;

import com.file.fileupload.domain.File;
import com.file.fileupload.service.IFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/common")
public class CommonController {
    @Autowired
    private IFileService fileService;
    @GetMapping("/file")
    public String file() {
        return "file";
    }


    @RequestMapping("/list")
    public String getList(Model model) throws ServletException, IOException {
        List<File> list=fileService.getList();

        model.addAttribute("list", list);
        return "download";
    }
}
