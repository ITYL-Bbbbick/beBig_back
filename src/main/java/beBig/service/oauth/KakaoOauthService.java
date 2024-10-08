package beBig.service.oauth;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@Slf4j
@Service
public class KakaoOauthService {

    // 카카오 REST API 키 <- 형이 바꿔야함
    @Value("${kakao.REST_API_KEY}")
    private String REST_API_KEY;

    // 카카오 로그인 후 리다이렉트될 URI <-이것도 형네주소
    @Value("${kakao.REDIRECT_URI}")
    private String REDIRECT_URI;

    public String getAccessToken(String authorize_code) {
        String access_Token = ""; // 엑세스 토큰을 저장할 변수
        String refresh_Token = ""; // 리프레시 토큰을 저장할 변수
        String reqURL = "https://kauth.kakao.com/oauth/token"; // 엑세스 토큰을 요청할 카카오 서버의 URL

        try {
            // 카카오 서버에 엑세스 토큰 요청을 위한 HTTP 연결 생성
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // POST 방식으로 요청
            conn.setRequestMethod("POST");
            conn.setDoOutput(true); // 데이터를 전송할 수 있도록 설정

            // 요청 파라미터 설정 (인증 코드, 클라이언트 ID, 리다이렉트 URI 등)
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=").append(REST_API_KEY);
            sb.append("&redirect_uri=").append(REDIRECT_URI);
            sb.append("&code=").append(authorize_code); // 카카오에서 받은 인증 코드를 포함
            bw.write(sb.toString());
            bw.flush(); // 버퍼에 있는 데이터를 전송

            System.out.println("ACCESS TOKEN 요청 URL : " + sb);

            // 응답 코드 확인 (정상 200)
            int responseCode = conn.getResponseCode();
            System.out.println("ACCESS TOKEN 응답 코드 : " + responseCode);

            // 응답 결과 읽기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            StringBuilder result = new StringBuilder();

            // 서버로부터 응답을 받아서 result에 저장
            while ((line = br.readLine()) != null) {
                result.append(line);
            }
            System.out.println("카카오 응답 body 의 내용 : " + result);

            // JSON 응답 파싱
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result.toString());

            // 응답에서 엑세스 토큰과 리프레시 토큰을 추출
            JsonObject jsonObject = element.getAsJsonObject();
            if (jsonObject.has("access_token") && jsonObject.has("refresh_token")) {
                access_Token = jsonObject.get("access_token").getAsString();
                refresh_Token = jsonObject.get("refresh_token").getAsString();

                System.out.println("access_token : " + access_Token);
                System.out.println("refresh_token : " + refresh_Token);
            } else {
                System.out.println("토큰 정보가 없습니다.");
            }

            // 리소스 닫기
            br.close();
            bw.close();
        } catch (IOException e) {
            // 네트워크 오류 처리
            System.out.println("네트워크 오류 발생: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            // 기타 예상치 못한 오류 처리
            System.out.println("예상치 못한 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }

        return access_Token; // 엑세스 토큰 반환
    }


    //사용자정보가져오기
    public JsonObject getUserInfo(String access_Token) {
        String reqURL = "https://kapi.kakao.com/v2/user/me"; // 사용자 정보 요청을 위한 카카오 API URL
        JsonObject userInfo = new JsonObject(); // 사용자 정보를 저장할 객체

        try {
            // 카카오 서버에 사용자 정보 요청을 위한 HTTP 연결 생성
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST"); // POST 방식으로 요청
            conn.setRequestProperty("Authorization", "Bearer " + access_Token); // 엑세스 토큰을 헤더에 추가하여 인증

            // 응답 코드 확인 (정상 200)
            int responseCode = conn.getResponseCode();
            System.out.println("유저 정보 요청 응답 코드 : " + responseCode);

            // 응답 결과 읽기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            StringBuilder result = new StringBuilder();

            // 응답 데이터를 result에 저장
            while ((line = br.readLine()) != null) {
                result.append(line);
            }
            System.out.println("유저 정보 요청 응답 Body 의 내용 : " + result);

            // JSON 응답 파싱
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result.toString());
            JsonObject response = element.getAsJsonObject();

            // 사용자 ID 추출
            if (response.has("id")) {
                long id = response.get("id").getAsLong();
                System.out.println("카카오 ID : " + id);
                userInfo.addProperty("id", id);
            }

            // 사용자 닉네임 추출
            JsonObject properties = response.getAsJsonObject("properties");
            if (properties != null && properties.has("nickname")) {
                String nickname = properties.get("nickname").getAsString();
                System.out.println("카카오 닉네임 : " + nickname);
                userInfo.addProperty("nickname", nickname);
            }

            // 리소스 닫기
            br.close();
        } catch (IOException e) {
            // 네트워크 오류 처리
            System.out.println("네트워크 오류 발생: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            // 기타 예상치 못한 오류 처리
            System.out.println("예상치 못한 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }

        return userInfo;  // 사용자 정보 반환 (오류 발생 시에도 수집된 정보 반환)
    }

    public Long kakaoLogout(String loginToken) {
        String logoutURL = "https://kapi.kakao.com/v1/user/logout";

        try {
            URL url = new URL(logoutURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + loginToken); // Bearer 토큰 설정
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"); // Content-Type 설정

            conn.setDoOutput(false);

            int responseCode = conn.getResponseCode();
            log.info("유저 정보 요청 응답 코드 : {}", responseCode);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder result = new StringBuilder();
            String line;

            // 응답 데이터를 result에 저장
            while ((line = br.readLine()) != null) {
                result.append(line);
            }
            br.close();

            log.info("응답 Body 내용: {}", result.toString());

            // 응답 데이터를 JSON으로 파싱하여 'id' 값 추출
            JsonObject jsonObject = JsonParser.parseString(result.toString()).getAsJsonObject();

            // 'id' 값을 추출하여 반환
            if (jsonObject.has("id")) {
                Long id = jsonObject.get("id").getAsLong();
                log.info("로그아웃 된 유저의 ID: {}", id);
                return id;
            } else {
                log.info("응답에 'id' 필드가 없습니다.");
                return -1L;
            }

        } catch (Exception e) {
            log.error("로그아웃 요청 중 에러 발생: {}", e.getMessage());
            return -1L;
        }
    }
}
