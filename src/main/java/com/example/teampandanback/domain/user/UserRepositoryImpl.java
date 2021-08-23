package com.example.teampandanback.domain.user;

import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;

import java.util.Optional;

import static com.example.teampandanback.domain.user.QUser.user;

public class UserRepositoryImpl implements UserRepositoryQuerydsl {
    private final JPAQueryFactory queryFactory;

    public UserRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Optional<User> getLastUser() {
        return Optional.ofNullable(queryFactory
                .select(user)
                .from(user)
                .orderBy(user.userId.desc())
                .fetchFirst());
    }
}
