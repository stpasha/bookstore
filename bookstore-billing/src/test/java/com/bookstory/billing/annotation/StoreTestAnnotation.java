package com.bookstory.billing.annotation;

import com.bookstory.billing.BillingApplication;
import com.bookstory.billing.config.TestConfig;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = BillingApplication.class)
@Import(TestConfig.class)
@ActiveProfiles("test")
public @interface StoreTestAnnotation {
}
