package kr.kro.personalos.service;

import java.time.LocalTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import kr.kro.personalos.entity.UserEntity;
import kr.kro.personalos.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserEntity createUser(String name, int age, double latitude, double longitude, String schoolName,
            String lctnNm, String pw) {
        UserEntity user = new UserEntity();
        user.setName(name);
        user.setAge(age);
        user.setLatitude(latitude);
        user.setLongitude(longitude);
        user.setSchoolNm(schoolName);
        user.setLctn(lctnNm);
        user.setPassword(StaticFunctions.getHash(pw));
        user.setHash(StaticFunctions.getHash(user.getName().toString() + user.getPassword().toString()
                + String.valueOf(getCurrentLongDate())));
        userRepository.save(user);
        return user;
    }

    public Optional<UserEntity> leave(String hash) {
        Optional<UserEntity> ou = userRepository.findByHash(hash);
        if (ou.isPresent()) {
            UserEntity user = ou.get();
            userRepository.delete(user);
            return Optional.of(user);
        } else {
            return Optional.empty();
        }
    }

    public Optional<UserEntity> findByHash(String hash) {
        return userRepository.findByHash(hash);
    }

    public Optional<UserEntity> getUser(String hash) {
        return userRepository.findByHash(hash);
    }

    private static long getCurrentLongDate() {
        long crt = Long.parseLong(LocalTime.now().toString().replace(":", "").replace(".", ""));
        return crt;
    }
}
