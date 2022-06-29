package dev.brighten.antibot.antibotconfirm.database;

import dev.brighten.antibot.antibotconfirm.database.objects.UserToConfirm;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<UserToConfirm, Integer> {

    List<UserToConfirm> findByName(String name);

    List<UserToConfirm> findByUuid(String uuid);
}
