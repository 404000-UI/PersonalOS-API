package kr.kro.personalos.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kr.kro.personalos.entity.UserEntity;
import kr.kro.personalos.service.UserService;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public UserEntity signup(@RequestParam(value = "name") String name,
            @RequestParam(value = "age") int age,
            @RequestParam(value = "latitude") double latitude, @RequestParam(value = "longitude") double longitude,
            @RequestParam(value = "schoolNm") String schoolNm, @RequestParam(value = "lctnNm") String lctnNm,@RequestParam(value = "pw") String pw) {
        return userService.createUser(name, age, latitude, longitude, schoolNm, lctnNm, pw);
    }

    @PostMapping("/leave")
    public Optional<UserEntity> leave(@RequestParam(value = "hash") String hash) {
        return userService.leave(hash);
    }

    @GetMapping("/signinWithHash")
    public Optional<UserEntity> signinWithHash(@RequestParam(value = "hash") String hash) {
        return userService.getUser(hash);
    }

}
