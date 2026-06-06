package kr.kro.personalos.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kr.kro.personalos.service.InfoService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MainController {

    private final InfoService infoService;

    @GetMapping("/info")
    public Optional<Map<String, Object>> getInfo(@RequestParam(value = "hash") String hash) {
        return infoService.getInfo(hash);
    }

}
