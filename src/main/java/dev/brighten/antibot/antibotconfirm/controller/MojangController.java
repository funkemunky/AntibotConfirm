package dev.brighten.antibot.antibotconfirm.controller;

import dev.brighten.antibot.antibotconfirm.database.MojangRepository;
import dev.brighten.antibot.antibotconfirm.database.objects.MojangUser;
import dev.brighten.antibot.antibotconfirm.utils.json.JSONArray;
import dev.brighten.antibot.antibotconfirm.utils.json.JSONException;
import dev.brighten.antibot.antibotconfirm.utils.json.JSONObject;
import dev.brighten.antibot.antibotconfirm.utils.json.JsonReader;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Controller
@Slf4j
public class MojangController {

    // Automatically injected by Spring for use of managing Mojang api caches.
    @Autowired
    private MojangRepository mojangRepository;

    public static MojangController INSTANCE;

    public MojangController() {
        INSTANCE = this;
    }

    public MojangUser getMojang(String name) {
        List<MojangUser> mojangUsers =mojangRepository.findByName(name);

        long now = System.currentTimeMillis();
        val iterator = mojangUsers.iterator();

        while(iterator.hasNext()) {
            MojangUser mojangUser = iterator.next();

            if(now - mojangUser.getUpdateTimestamp() > TimeUnit.DAYS.toMillis(14)) {
                mojangRepository.delete(mojangUser);
                iterator.remove();
            }
        }

        if(mojangUsers.size() > 0) {
            return mojangUsers.get(0);
        } else {
            MojangUser user = new MojangUser();

            UUID uuid = lookupUUID(name);

            user.setUuid(uuid.toString());
            user.setName(name);
            user.setUpdateTimestamp(System.currentTimeMillis());

            mojangRepository.save(user);

            return user;
        }
    }

    public MojangUser getMojang(UUID uuid) {
        List<MojangUser> mojangUsers =mojangRepository.findByUuid(uuid.toString());

        long now = System.currentTimeMillis();
        val iterator = mojangUsers.iterator();

        while(iterator.hasNext()) {
            MojangUser mojangUser = iterator.next();

            if(now - mojangUser.getUpdateTimestamp() > TimeUnit.DAYS.toMillis(14)) {
                mojangRepository.delete(mojangUser);
                iterator.remove();
            }
        }

        if(mojangUsers.size() > 0) {
            return mojangUsers.get(0);
        } else {
            MojangUser user = new MojangUser();

            String name = lookupName(uuid);

            user.setUuid(uuid.toString());
            user.setName(name);
            user.setUpdateTimestamp(System.currentTimeMillis());

            mojangRepository.save(user);

            return user;
        }
    }

    public UUID formatFromMojangUUID(String mojangUUID) {
        String uuid = "";
        for(int i = 0; i <= 31; i++) {
            uuid = uuid + mojangUUID.charAt(i);
            if(i == 7 || i == 11 || i == 15 || i == 19) {
                uuid = uuid + "-";
            }
        }

        return UUID.fromString(uuid);
    }

    public String formatToMojangUUID(String uuid) {
        return uuid.replace("-", "");
    }

    public String formatToMojangUUID(UUID uuid) {
        return formatToMojangUUID(uuid.toString());
    }

    public UUID lookupUUID(String playername) {
        try {
            JSONObject object = JsonReader
                    .readJsonFromUrl("https://api.mojang.com/users/profiles/minecraft/" + playername);

            if(object.has("id")) {
                UUID uuid = formatFromMojangUUID(object.getString("id"));

                return uuid;
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void readData(String toRead, StringBuilder result) {
        int i = 7;

        while (i < 200) {
            if (!String.valueOf(toRead.charAt(i)).equalsIgnoreCase("\"")) {

                result.append(String.valueOf(toRead.charAt(i)));

            } else {
                break;
            }

            i++;
        }
    }

    private String lookupName(UUID id) {
        if (id == null) {
            return null;
        } else {
            try {
                URLConnection conn = new URL("https://api.mojang.com/user/profiles/"
                        + formatToMojangUUID(id) + "/names").openConnection();

                conn.setConnectTimeout(2000);
                conn.setReadTimeout(3000);

                InputStream is = conn.getInputStream();

                BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));

                String jsonText = JsonReader.readAll(rd);

                val array = new JSONArray(jsonText);
                return array.getJSONObject(array.length() - 1).getString("name");
            } catch (MalformedURLException var12) {
                log.warn("Malformed URL in UUID lookup");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
