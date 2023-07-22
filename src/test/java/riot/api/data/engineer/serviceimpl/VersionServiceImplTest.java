package riot.api.data.engineer.serviceimpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.reactive.function.client.WebClient;
import riot.api.data.engineer.entity.Version;
import riot.api.data.engineer.dto.api.ApiInfo;
import riot.api.data.engineer.repository.VersionQueryRepository;
import riot.api.data.engineer.repository.VersionRepository;
import riot.api.data.engineer.service.VersionService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application.yml")
class VersionServiceImplTest {

    @MockBean
    private VersionQueryRepository versionQueryRepository;
    @MockBean
    private VersionRepository versionRepository;
    private VersionService versionService;

    @BeforeEach
    void setVersionService() {
    }

    @Test
    @DisplayName("현재 버전 조회 테스트")
    void testFindOneByCurrentVersion() {
        // Given
        Version version = new Version("13.13.1", true);
        when(versionQueryRepository.findOneByCurrentVersion()).thenReturn(version);
        // When
        Version result = versionService.findOneByCurrentVersion();

        // Then
        assertEquals(version.getVersion(), result.getVersion(), "버전 비교");
        assertEquals(version.getCurrentVersionYn(), result.getCurrentVersionYn(), "true/false");
    }


}