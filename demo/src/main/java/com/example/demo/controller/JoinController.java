package com.example.demo.controller;

import com.example.demo.dto.UserDTO;
import com.example.demo.service.JoinService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
public class JoinController {

    private final JoinService joinService;

    public JoinController(JoinService joinService) {

        this.joinService = joinService;
    }

    @PostMapping("join")
    public String joinProcess(UserDTO userDTO) {

        joinService.joinProcess(userDTO);

        return "ok";
    }
}
