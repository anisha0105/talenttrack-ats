package com.talenttrack.ats;

import com.talenttrack.ats.auth.JwtService;
import com.talenttrack.ats.user.Role;
import com.talenttrack.ats.user.UserAccount;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {
    @Test
    void tokenRetainsAuthenticatedEmail() {
        JwtService service = new JwtService("a-test-secret-key-that-is-longer-than-32-bytes", 1);
        UserAccount user = new UserAccount("candidate@talenttrack.dev", "hash", "Candidate User",
                Role.CANDIDATE, null, null);

        String token = service.issue(user);

        assertThat(service.subject(token)).isEqualTo("candidate@talenttrack.dev");
    }
}
