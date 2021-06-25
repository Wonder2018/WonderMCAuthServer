package top.imwonder.mcauth.controller.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import top.imwonder.mcauth.domain.Token;
import top.imwonder.mcauth.domain.User;
import top.imwonder.mcauth.enumeration.LoginType;
import top.imwonder.mcauth.exception.WonderMcException;
import top.imwonder.mcauth.pojo.ProfileInfo;
import top.imwonder.mcauth.pojo.requestbody.AuthenticateReq;
import top.imwonder.mcauth.pojo.requestbody.RefreshReq;
import top.imwonder.mcauth.pojo.requestbody.Validate;
import top.imwonder.mcauth.pojo.responsebody.AuthenticateRes;
import top.imwonder.mcauth.pojo.responsebody.RefreshRes;
import top.imwonder.mcauth.services.ProfileInfoService;
import top.imwonder.mcauth.services.TokenService;
import top.imwonder.mcauth.services.UserInfoService;

@RestController
@RequestMapping("/api/authserver")
public class UserController {

    @Autowired
    private TokenService tokens;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private ProfileInfoService profileInfoService;

    @PostMapping("/authenticate")
    public AuthenticateRes authenticate(@RequestBody AuthenticateReq req) {
        AuthenticateRes res = new AuthenticateRes();
        LoginType type = userInfoService.verifyPwdForType(req.getUsername(), req.getPassword());
        if (type == LoginType.FAIL) {
            throw WonderMcException.forbiddenOperationException("账号或密码错误");
        }
        User user = userInfoService.loadUser(req.getUsername(), type);
        Token token = tokens.createToken(user.getId(), req.getClientToken());
        List<ProfileInfo> profiles = profileInfoService.loadAllProfileOfUser(user.getId(), true, true);
        res.setAccessToken(token.getAccessToken());
        res.setClientToken(token.getClientToken());
        res.setAvailableProfiles(profiles);

        if (profiles.size() == 1) {
            res.setSelectedProfile(profiles.get(0));
            tokens.bindProfile(token, profiles.get(0).getId());
        }
        if (type == LoginType.PROFILE_NAME) {
            res.setSelectedProfile(profileInfoService.loadProfileByName(req.getUsername(), true, true));
            tokens.bindProfile(token, res.getSelectedProfile().getId());
        }
        if (req.getRequestUser()) {
            res.setUser(userInfoService.createUserInfo(token.getUid()));
        }
        return res;
    }

    @PostMapping("/refresh")
    public RefreshRes refresh(RefreshReq ref) {
        String accessToken = ref.getAccessToken();
        String clientToken = ref.getClientToken();
        Token token = tokens.querySessionByAccessToken(accessToken);
        if (token == null || (clientToken != null && !token.getClientToken().equals(clientToken))) {
            throw WonderMcException.forbiddenOperationException("令牌无效");
        }
        RefreshRes res = new RefreshRes();
        ProfileInfo profileInfo = ref.getSelectedProfile();
        if (profileInfo != null) {
            tokens.bindProfile(token, profileInfo.getId());
        }
        tokens.updateToken(token);
        res.setAccessToken(token.getAccessToken());
        res.setClientToken(token.getClientToken());
        if (ref.getRequestUser()) {
            res.setUser(userInfoService.createUserInfo(token.getUid()));
        }
        return res;
    }

    @PostMapping("/validate")
    public ResponseEntity<Void> validate(Validate val) {
        String accessToken = val.getAccessToken();
        String clientToken = val.getClientToken();
        Token token = tokens.querySessionByAccessToken(accessToken);
        if (token == null || (clientToken != null && !token.getClientToken().equals(clientToken))) {
            throw WonderMcException.forbiddenOperationException("令牌无效");
        }
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/invalidate")
    public ResponseEntity<Void> invalidate(Validate val) {
        tokens.removeToken(val.getAccessToken());
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/signout")
    public ResponseEntity<Void> signout(String username, String password) {
        if (userInfoService.verifyPwd(username, password)) {
            return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
        }
        throw WonderMcException.forbiddenOperationException("账号或密码错误");
    }
}
