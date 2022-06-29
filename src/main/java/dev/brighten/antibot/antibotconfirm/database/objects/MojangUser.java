package dev.brighten.antibot.antibotconfirm.database.objects;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
public class MojangUser {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;

    @Setter
    private String name;
    @Setter
    private String uuid;
    @Setter
    private long updateTimestamp;
}
