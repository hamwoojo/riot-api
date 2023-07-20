package riot.api.data.engineer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import riot.api.data.engineer.dto.api.ApiKey;

public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {
}
