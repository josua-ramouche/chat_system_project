package Controller.Database;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseControllerTest {
    @BeforeAll
    static void ensure_database_is_created() {
        DatabaseController.createUserTable();
    }
    @Test
    void connect() {
    }

    @Test
    void createUserTable() {
    }

    @Test
    void createContactTable() {
    }

    @Test
    void createChatTable() {
    }

}