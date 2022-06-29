package dev.brighten.antibot.antibotconfirm.database.objects;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Entity
public class UserToConfirm {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;

    @Setter
    private String uuid;
    @Setter
    private String ip;
    @Setter
    private String name;
    @Setter
    private long startTime;
    @Setter
    private boolean confirmed;
}
