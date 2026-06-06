package kr.kro.personalos.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.kro.personalos.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    public Optional<UserEntity> findByHash(String hash);
}
