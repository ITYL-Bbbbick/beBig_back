package beBig.service;

import beBig.form.UserForm;
import beBig.mapper.UserMapper;
import beBig.vo.UserVo;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class UserService {

    private final UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }


    public void registerUser(UserForm userForm) throws Exception {

        String encryptedPassword = passwordEncoder.encode(userForm.getPassword());

        // 새로운 사용자 객체 생성
        UserVo user = new UserVo();
        user.setUserName(userForm.getName());
        user.setUserNickname(userForm.getNickname());
        user.setUserId(userForm.getUserId());
        user.setUserPassword(encryptedPassword);// 비밀번호 암호화 필요
        user.setUserEmail(userForm.getEmail());
        user.setUserGender(userForm.isGender());
        user.setUserBirth(userForm.getBirth());
        user.setUserFinTypeCode(0);
        user.setUserBadgeCode(0);
        // 사용자 정보 저장
        userMapper.insert(user);
        log.info("user 저장성공");
        log.info("user의 이름은 : " + user.getUserName());
        log.info(("user의 password는 : " + encryptedPassword));
    }

    public UserVo findByUserId(String userId) throws Exception {
        return userMapper.findByUserId(userId);
    }
}
