package kr.kro.personalos.service;

import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import kr.kro.personalos.entity.UserEntity;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InfoService {

    private final UserService userService;
    private final StaticFunctions staticFunctions;
    
    public Optional<Map<String, Object>> getInfo(String hash) {
        Optional<UserEntity> u = userService.findByHash(hash);
        if (u.isPresent()) {
            UserEntity user = u.get();
            Map<String, Object> weatherInfo = staticFunctions.getWeatherInfo(user.getLatitude(), user.getLongitude());
            Map<String, Object> schoolInfo = staticFunctions.getSchoolInfo(user.getSchoolNm(), user.getLctn());
            Map<String, Object> result = Map.of(
                    "weatherInfo", weatherInfo,
                    "schoolInfo", schoolInfo);
            return Optional.of(result);
        } else {
            return Optional.empty();
        }
    }
    
}
