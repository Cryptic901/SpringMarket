package by.cryptic.userservice.model.read;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;


@Getter
@Setter
@ToString
@Builder
@Document(collection = "user_view")
@AllArgsConstructor
@NoArgsConstructor
public class AppUserView {

    @MongoId
    private UUID userId;

    private String username;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    private LocalDateTime createdAt;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AppUserView that = (AppUserView) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(username, that.username) &&
                Objects.equals(phoneNumber, that.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, username, phoneNumber);
    }
}
