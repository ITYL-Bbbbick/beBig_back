package beBig.service;


import beBig.dto.response.MyPageEditResponseDto;
import beBig.dto.response.MyPagePostResponseDto;
import beBig.dto.response.UserProfileResponseDto;
import beBig.vo.BadgeVo;

import java.util.List;

public interface MyPageService {
    List<BadgeVo> getBadges();

    UserProfileResponseDto findProfileByUserId(long userId);

    List<MyPagePostResponseDto> findMyPostByUserId(long userId);

    List<MyPagePostResponseDto> findMyLikeHitsByUserId(long userId);

    String findLoginIdByUserId(long userId);

    MyPageEditResponseDto findEditFormByUserId(long userId);

    void saveMyPageSocial(long userId, String userIntro, String userNickname);

    void saveMyPageGeneral(long userId, String userIntro, String userNickname, String password);

    boolean checkPassword(String password, long userId);

    void saveMyPageGeneralWithoutPassword(long userId, String userIntro, String userNickname);

    void updateVisibility(long userId, int userVisibility);
}
