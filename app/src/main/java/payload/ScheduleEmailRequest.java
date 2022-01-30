package payload;

import java.time.LocalDateTime;
import java.time.ZoneId;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @EqualsAndHashCode @ToString
@NoArgsConstructor
public class ScheduleEmailRequest {
    
    @Email
    @NotEmpty
    @Getter(AccessLevel.PUBLIC)
    @JsonProperty("email")
    private String email;

    @NotEmpty
    @Getter(AccessLevel.PUBLIC)
    @JsonProperty("subject")
    private String subject;

    @NotEmpty
    @Getter(AccessLevel.PUBLIC)
    private String body;

    @NotNull
    @Getter(AccessLevel.PUBLIC)
    @JsonProperty("datetime")
    private LocalDateTime dateTime;

    @NotNull
    @Getter(AccessLevel.PUBLIC)
    @JsonProperty("timezone")
    private ZoneId timeZone;
	
	// Getters and Setters (Omitted for brevity)
    // public String getEmail() {return email;}
    // public String getSubject() {return subject;}
    // public String getBody() {return body;}
    // public LocalDateTime getDateTime() {return dateTime;}
    // public ZoneId getTimeZone() {return timeZone;}
}
