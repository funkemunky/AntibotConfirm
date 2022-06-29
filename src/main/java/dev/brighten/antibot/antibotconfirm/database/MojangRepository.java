package dev.brighten.antibot.antibotconfirm.database;

import dev.brighten.antibot.antibotconfirm.database.objects.MojangUser;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MojangRepository extends CrudRepository<MojangUser, Integer> {

    List<MojangUser> findByName(String name);

    List<MojangUser> findByUuid(String uuid);
}
