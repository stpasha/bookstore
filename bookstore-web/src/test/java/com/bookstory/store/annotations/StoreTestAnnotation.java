package com.bookstory.store.annotations;

import com.bookstory.store.StoreApplication;
import com.bookstory.store.config.TestConfig;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = StoreApplication.class)
@Import(TestConfig.class)
@ActiveProfiles("test")
public @interface StoreTestAnnotation {
}
