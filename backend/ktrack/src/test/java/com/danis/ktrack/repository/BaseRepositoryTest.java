package com.danis.ktrack.repository;



import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@Transactional
@Rollback
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)

public abstract class BaseRepositoryTest {
}
