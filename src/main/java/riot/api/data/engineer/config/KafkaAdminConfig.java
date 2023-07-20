package riot.api.data.engineer.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;
import riot.api.data.engineer.entity.KafkaInfo;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaAdminConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    private static final int PARTITION_COUNT = 5;
    private static final int REPLICA_COUNT = 3;

    @Bean
    public KafkaAdmin admin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic setMatchTopic() {
        return TopicBuilder.name(KafkaInfo.TopicName.MATCHS.getKafkaTopicName())
                .partitions(PARTITION_COUNT)
                .replicas(REPLICA_COUNT)
                .compact()
                .build();
    }

    @Bean
    public NewTopic setRunes() {
        return TopicBuilder.name(KafkaInfo.TopicName.RUNES.getKafkaTopicName())
                .partitions(PARTITION_COUNT)
                .replicas(REPLICA_COUNT)
                .compact()
                .build();
    }

    @Bean
    public NewTopic setChampions() {
        return TopicBuilder.name(KafkaInfo.TopicName.CHAMPIONS.getKafkaTopicName())
                .partitions(PARTITION_COUNT)
                .replicas(REPLICA_COUNT)
                .compact()
                .build();
    }

    @Bean
    public NewTopic setItems() {
        return TopicBuilder.name(KafkaInfo.TopicName.ITEMS.getKafkaTopicName())
                .partitions(PARTITION_COUNT)
                .replicas(REPLICA_COUNT)
                .compact()
                .build();

    }

    @Bean
    public NewTopic setSpells() {
        return TopicBuilder.name(KafkaInfo.TopicName.SPELLS.getKafkaTopicName())
                .partitions(PARTITION_COUNT)
                .replicas(REPLICA_COUNT)
                .compact()
                .build();
    }



}
