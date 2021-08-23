package com.example.teampandanback.domain.user;

import java.util.Optional;

public interface UserRepositoryQuerydsl{

    Optional<User> getLastUser();
}
