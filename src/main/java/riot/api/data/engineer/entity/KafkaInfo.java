package riot.api.data.engineer.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity(name = "kafka_info")
@Getter
@Setter
public class KafkaInfo {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @Column(name = "topic_name")
    private String topicName;
    @Column(name = "partition")
    private Integer partition;
    @Column(name = "replicas")
    private Integer replicas;
    @Column(name = "api_info_id")
    private Long apiInfoId;

    public enum TopicName{
        MATCHS("matchs"),
        RUNES("runes"),
        CHAMPIONS("champions"),
        ITEMS("items"),
        SPELLS("spells");
        final String kafkaTopicName;
        TopicName(String kafkaTopicName) {
            this.kafkaTopicName = kafkaTopicName;
        }
        public String getKafkaTopicName() {
            return kafkaTopicName;
        }
    }


}
