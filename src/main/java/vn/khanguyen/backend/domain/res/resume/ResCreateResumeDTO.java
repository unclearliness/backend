package vn.khanguyen.backend.domain.res.resume;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResCreateResumeDTO {
    private long id;
    private Instant createdAt;
    private String createBy;
}

