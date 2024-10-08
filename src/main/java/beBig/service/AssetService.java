package beBig.service;

import beBig.dto.AssetAnalysisDto;
import beBig.dto.UserTotalAssetsDto;
import beBig.dto.response.SpendingPatternsResponseDto;
import beBig.vo.UserVo;

import java.util.Map;

public interface AssetService {
    public AssetAnalysisDto showAnalysis(long userId);
    public SpendingPatternsResponseDto showSpendingPatterns(long userId,int year);
    public Map<String, Object> showProductRecommendations(long userId);
    public UserTotalAssetsDto showAgeComparison(long userId);
}
