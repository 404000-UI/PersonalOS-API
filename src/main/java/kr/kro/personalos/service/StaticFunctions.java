package kr.kro.personalos.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class StaticFunctions {

    private final Dotenv dotenv;

    public Map<String, Object> getWeatherInfo(double latitude, double longitude) {
        final String _apiKey = dotenv.get("GET_WEATHER_API_KEY");
        final String _pageNo = "1";
        final String _numOfRows = "1000";
        final String _dateType = "JSON";
        final String _base_date = LocalDate.now().toString().replace("-", "");
        final String _base_time = String.format("%02d00", LocalTime.now().getHour());
        final List<String> nxy = latLonToGrid(latitude, longitude);
        final String _URL = String.format(
                "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst?ServiceKey=%s&nx=%s&ny=%s&pageNo=%s&numOfRows=%s&dataType=%s&base_date=%s&base_time=%s",
                _apiKey, nxy.get(0), nxy.get(1), _pageNo, _numOfRows, _dateType, _base_date, _base_time);

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(_URL)).GET().build();
        try {
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.body());
            JsonNode items = root.path("response").path("body").path("items").path("item");
            Map<String, String> result = new HashMap<>();
            for (JsonNode item : items) {
                result.put(item.path("category").asString(), item.path("obsrValue").asString());
            }
            Map<String, Object> returnMap = Map.of(
                    "time", _base_time,
                    "latitude", latitude,
                    "longtitude", longitude,
                    "result", result);
            return returnMap;
        } catch (IOException e) {
            System.out.println("IOException");
        } catch (InterruptedException e) {
            System.out.println("InterruptedException");
        }

        return Map.of();
    }

    public Map<String, Object> getSchoolInfo(String schoolName, String lctnName) {
        final String _apiKey = dotenv.get("GET_SCHOOL_INFO_API_KEY");
        final String _type = "json";
        final String _pIndex = "1";
        final String _pSize = "1000";
        final String _schulNm = schoolName;
        final String _lctnScNm = lctnName;
        final String _URL = String.format(
                "https://open.neis.go.kr/hub/schoolInfo?KEY=%s&Type=%s&pIndex=%s&pSize=%s&SCHUL_NM=%s&LCTN_SC_NM=%s",
                _apiKey, _type, _pIndex, _pSize, _schulNm, _lctnScNm);

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(_URL)).GET().build();
        try {
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.body());
            JsonNode items = root.path("schoolInfo").get(1).path("row").get(0);
            Map<String, Object> returnMap = mapper.convertValue(items,
                    new TypeReference<HashMap<String, Object>>() {
                    });
            returnMap.put("dietInfo", getSchoolDietInfo(returnMap.get("ATPT_OFCDC_SC_CODE").toString(),
                    returnMap.get("SD_SCHUL_CODE").toString(), LocalDate.now().toString().replace("-", "")));
            return returnMap;
        } catch (IOException e) {
            System.out.println("IOException");
        } catch (InterruptedException e) {
            System.out.println("InterruptedException");
        }
        return Map.of();
    }

    private Map<String, Object> getSchoolDietInfo(String atptCode, String schulCode, String ymd) {
        if (LocalDate.now().getDayOfWeek() == DayOfWeek.SATURDAY
                || LocalDate.now().getDayOfWeek() == DayOfWeek.SUNDAY) {
            return Map.of();
        }
        final String _apiKey = dotenv.get("GET_SCHOOL_INFO_API_KEY");
        final String _type = "json";
        final String _pIndex = "1";
        final String _pSize = "100";
        final String _atptCode = atptCode;
        final String _schulCode = schulCode;
        final String _ymd = ymd;
        final String _URL = String.format(
                "https://open.neis.go.kr/hub/mealServiceDietInfo?KEY=%s&Type=%s&pIndex=%s&pSize=%s&ATPT_OFCDC_SC_CODE=%s&SD_SCHUL_CODE=%s&MLSV_YMD=%s",
                _apiKey, _type, _pIndex, _pSize, _atptCode, _schulCode, _ymd);

        System.out.println(_apiKey);

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(_URL)).GET().build();
        try {
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.body());
            JsonNode items = root.path("mealServiceDietInfo").get(1).path("row").get(0);
            Map<String, Object> map = mapper.convertValue(items, new TypeReference<Map<String, Object>>() {
            });
            Map<String, Object> returnMap = Map.of(
                    "dish", map.get("DDISH_NM"),
                    "cal", map.get("CAL_INFO"),
                    "ntr", map.get("NTR_INFO"));
            return returnMap;
        } catch (IOException e) {
            System.out.println("IOException");
        } catch (InterruptedException e) {
            System.out.println("InterruptedException");
        }
        return Map.of();
    }

    public static String getHash(String pw) {
        String result = null;
        MessageDigest md = null;

        try {
            md = MessageDigest.getInstance("SHA-512");
            byte[] bytes = pw.getBytes(Charset.forName("UTF-8"));
            md.update(bytes);
            result = Base64.getEncoder().encodeToString(md.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return result;
    }

    private static List<String> latLonToGrid(double lat, double lon) {

        final double RE = 6371.00877;
        final double GRID = 5.0;

        final double SLAT1 = 30.0;
        final double SLAT2 = 60.0;

        final double OLON = 126.0;
        final double OLAT = 38.0;

        final double XO = 43;
        final double YO = 136;

        double DEGRAD = Math.PI / 180.0;

        double re = RE / GRID;

        double slat1 = SLAT1 * DEGRAD;
        double slat2 = SLAT2 * DEGRAD;
        double olon = OLON * DEGRAD;
        double olat = OLAT * DEGRAD;

        double sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5)
                / Math.tan(Math.PI * 0.25 + slat1 * 0.5);

        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);

        double sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;

        double ro = Math.tan(Math.PI * 0.25 + olat * 0.5);
        ro = re * sf / Math.pow(ro, sn);

        double ra = Math.tan(Math.PI * 0.25 + lat * DEGRAD * 0.5);
        ra = re * sf / Math.pow(ra, sn);

        double theta = lon * DEGRAD - olon;

        if (theta > Math.PI)
            theta -= 2.0 * Math.PI;
        if (theta < -Math.PI)
            theta += 2.0 * Math.PI;

        theta *= sn;

        int x = (int) (ra * Math.sin(theta) + XO + 0.5);
        int y = (int) (ro - ra * Math.cos(theta) + YO + 0.5);

        return List.of(String.valueOf(x), String.valueOf(y));
    }
}