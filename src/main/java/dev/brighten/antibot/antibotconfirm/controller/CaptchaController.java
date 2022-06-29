package dev.brighten.antibot.antibotconfirm.controller;

import dev.brighten.antibot.antibotconfirm.database.UserRepository;
import dev.brighten.antibot.antibotconfirm.database.objects.MojangUser;
import dev.brighten.antibot.antibotconfirm.database.objects.UserToConfirm;
import dev.brighten.antibot.antibotconfirm.utils.json.JSONException;
import dev.brighten.antibot.antibotconfirm.utils.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Controller
@RequestMapping("/captcha")
public class CaptchaController {

    // For accessing UserToConfirm. Automatically injected by Spring
    @Autowired
    private UserRepository userRepository;

    /**
     * Controller for starting a captcha confirmation from the client side.
     *
     * @param uuid String of player UUID provided from url params
     * @param ip String of player IP provided from url params
     * @return
     */
    @RequestMapping(value = "/start", method = RequestMethod.POST)
    @ResponseBody
    public CompletableFuture<String> captcha(@RequestParam(value = "uuid", defaultValue = "none") String uuid,
                                             @RequestParam(value = "ip", defaultValue = "127.0.0.1") String ip) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                JSONObject object = new JSONObject();

                if(uuid.equals("none")) {
                    object.put("error", true);
                    object.put("errorReason", "INVALID_UUID");

                    return object.toString();
                }

                MojangUser mojang = MojangController.INSTANCE.getMojang(UUID.fromString(uuid));

                List<UserToConfirm> users = userRepository.findByUuid(uuid);
                if(users.size() > 0) {
                    userRepository.deleteAll(users);
                    users.clear();
                }

                UserToConfirm user = new UserToConfirm();
                user.setName(mojang.getName());
                user.setUuid(mojang.getUuid());
                user.setIp(ip);
                user.setStartTime(System.currentTimeMillis());
                user.setConfirmed(false);

                userRepository.save(user);

                object.put("error", false);
                object.put("id", user.getId());

                return object.toString();

            } catch(JSONException e) {
                e.printStackTrace();
                return "jsonerror";
            }
        });
    }

}
