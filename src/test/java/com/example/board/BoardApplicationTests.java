package com.example.board;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:board;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",

        "spring.jpa.hibernate.ddl-auto=create-drop",

        "jwt.secret=dGVzdC1qd3Qtc2VjcmV0LWtleS1tdXN0LWJlLWF0LWxlYXN0LTMyLWJ5dGVz",

        "app.image.cleanup.batch-size=10",

        "app.file.storage=local",
        "app.file.local.root-path=./build/test-uploads",
        "app.file.local.public-base-url=http://localhost:8080/files"
})
class BoardApplicationTests {

    @Test
    void contextLoads() {
    }
}