package cz.cvut.kbss.ear.meeting_scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MeetingSchedulerApplication {

    public static void main(String[] args) throws JsonProcessingException {
        SpringApplication.run(MeetingSchedulerApplication.class, args);
    }
}
