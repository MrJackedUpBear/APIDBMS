package src;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.HashMap;

import java.util.Base64;

public class Barcode {
    private static final URL authUrl;

    static {
        try {
            authUrl = new URI("http://localhost:8080/BarcodeServlet/AuthServlet/").toURL();
        } catch (URISyntaxException | MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private static final URL tableUrl;

    static {
        try {
            tableUrl = new URI("http://localhost:8080/BarcodeServlet/Table/PartsTable").toURL();
        } catch (MalformedURLException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getToken(String username, String password) throws IOException {
        //addToTable("BBY", 192.49, "7");
        HttpURLConnection authConnection = (HttpURLConnection) authUrl.openConnection();

        authConnection.setRequestMethod("POST");

        String auth = username + ":" + password;
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));

        String authHeaderValue = "Basic " + new String(encodedAuth);

        authConnection.setRequestProperty("authorization", authHeaderValue);

        int responseCode = authConnection.getResponseCode();

        String token;
        if (responseCode == 200) {
            Scanner scanner = new Scanner(authConnection.getInputStream());
            token = scanner.nextLine();
            scanner.close();
        }
        else{
            return "Unauthorized User";
        }

        return token;
    }

    public static HashMap<Integer, HashMap<String, String>> getPart(String token, String partNumber) throws IOException {
        if (token.isEmpty()){
            return null;
        }

        HttpURLConnection conn = (HttpURLConnection) tableUrl.openConnection();
        conn.setRequestMethod("POST");

        byte[] encodedToken = Base64.getEncoder().encode(token.getBytes(StandardCharsets.UTF_8));
        String authHeaderValue = "Basic " + new String(encodedToken);

        conn.setRequestProperty("authorization", authHeaderValue);

        String postData = "Action=" + URLEncoder.encode("Get Part", StandardCharsets.UTF_8);
        postData += "&Part Number=" + URLEncoder.encode(partNumber, StandardCharsets.UTF_8);

        conn.setDoOutput(true);
        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
        wr.write(postData);
        wr.flush();

        int responseCode = conn.getResponseCode();

        StringBuilder response = new StringBuilder();

        if (responseCode == 200) {
            Scanner sc = new Scanner(conn.getInputStream());
            while (sc.hasNextLine()) {
                response.append(sc.nextLine());
            }
            sc.close();
        }

        if (response.toString().equals("{}")){
            return null;
        }
        return stringToHashMap(response.toString());
    }

    public static HashMap<Integer, HashMap<String, String>> getTable(String token) throws IOException {
        if (token.isEmpty()){
            return null;
        }

        HttpURLConnection conn = (HttpURLConnection) tableUrl.openConnection();
        conn.setRequestMethod("GET");

        byte[] encodedToken = Base64.getEncoder().encode(token.getBytes(StandardCharsets.UTF_8));
        String authHeaderValue = "Basic " + new String(encodedToken);

        conn.setRequestProperty("authorization", authHeaderValue);

        StringBuilder response = new StringBuilder();

        int responseCode = conn.getResponseCode();

        if (responseCode == 200){
            Scanner scanner = new Scanner(conn.getInputStream());
            while (scanner.hasNextLine()){
                response.append(scanner.nextLine());
            }
            scanner.close();
        }

        response = new StringBuilder(response.substring(1, response.length() - 1));

        if (response.isEmpty()){
            return new HashMap<>();
        }

        return stringToHashMap(response.toString());
    }

    public static String addToTable(String token, String partNumber, String partType, String partDesc, String partNote) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) tableUrl.openConnection();
        conn.setRequestMethod("POST");

        byte[] encodedToken = Base64.getEncoder().encode(token.getBytes(StandardCharsets.UTF_8));
        String authHeaderValue = "Basic " + new String(encodedToken);

        conn.setRequestProperty("authorization", authHeaderValue);

        String postData = "Action=" + URLEncoder.encode("Add", StandardCharsets.UTF_8);
        postData += "&Part Number=" + URLEncoder.encode(partNumber, StandardCharsets.UTF_8);
        postData += "&Part Type=" + URLEncoder.encode(partType, StandardCharsets.UTF_8);
        postData += "&Part Description=" + URLEncoder.encode(partDesc, StandardCharsets.UTF_8);
        postData += "&Part Note=" + URLEncoder.encode(partNote, StandardCharsets.UTF_8);

        conn.setDoOutput(true);
        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
        wr.write(postData);
        wr.flush();

        int responseCode = conn.getResponseCode();

        if (responseCode == 200){
            return "POST Succeeded";
        }
        else if (responseCode == 401){
            return "Not authorized";
        }
        else{
            return "Response code: " + responseCode;
        }
    }

    public static String addUser(String token, String user, String password, int updateWireTableAccess, int updatePartsTableAccess,int addUserAccess, int deleteUserAccess) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) tableUrl.openConnection();
        conn.setRequestMethod("POST");

        byte[] encodedToken = Base64.getEncoder().encode(token.getBytes(StandardCharsets.UTF_8));
        String authHeaderValue = "Basic " + new String(encodedToken);

        conn.setRequestProperty("authorization", authHeaderValue);

        String postData = "Action=" + URLEncoder.encode("Add User", StandardCharsets.UTF_8);
        postData += "&New Username=" + URLEncoder.encode(user, StandardCharsets.UTF_8);
        postData += "&New Password=" + URLEncoder.encode(password, StandardCharsets.UTF_8);
        postData += "&Modify Wire Table=" + URLEncoder.encode(String.valueOf(updateWireTableAccess), StandardCharsets.UTF_8);
        postData += "&Modify Parts Table=" + URLEncoder.encode(String.valueOf(updatePartsTableAccess), StandardCharsets.UTF_8);
        postData += "&Modify Users=" + URLEncoder.encode(String.valueOf(addUserAccess), StandardCharsets.UTF_8);
        postData += "&Delete Users=" + URLEncoder.encode(String.valueOf(deleteUserAccess), StandardCharsets.UTF_8);

        conn.setDoOutput(true);
        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
        wr.write(postData);
        wr.flush();

        int responseCode = conn.getResponseCode();

        if (responseCode == 200){
            return "POST Succeeded";
        }
        else if (responseCode == 401){
            return "Not Authorized";
        }
        else{
            return "Response code: " + responseCode;
        }
    }

    public static String changePassword(String token, String oldPassword, String newPassword) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) tableUrl.openConnection();
        conn.setRequestMethod("POST");

        byte[] encodedToken = Base64.getEncoder().encode(token.getBytes(StandardCharsets.UTF_8));
        String authHeaderValue = "Basic " + new String(encodedToken);

        conn.setRequestProperty("authorization", authHeaderValue);

        String postData = "Action=" + URLEncoder.encode("Change Password", StandardCharsets.UTF_8);
        postData += "&Old Password=" + URLEncoder.encode(oldPassword, StandardCharsets.UTF_8);
        postData += "&New Password=" + URLEncoder.encode(newPassword, StandardCharsets.UTF_8);

        conn.setDoOutput(true);
        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
        wr.write(postData);
        wr.flush();

        int responseCode = conn.getResponseCode();

        if (responseCode == 200){
            return "POST Succeeded";
        }
        else if (responseCode == 401){
            return "Not Authorized";
        }
        else{
            return "Response code: " + responseCode;
        }
    }

    public static String updateTable(String token, String partType, String partDesc, String partNote, String partNumber) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) tableUrl.openConnection();
        conn.setRequestMethod("POST");

        byte[] encodedToken = Base64.getEncoder().encode(token.getBytes(StandardCharsets.UTF_8));
        String authHeaderValue = "Basic " + new String(encodedToken);

        conn.setRequestProperty("authorization", authHeaderValue);

        String postData = "Action=" + URLEncoder.encode("update", StandardCharsets.UTF_8);
        postData += "&Part Type=" + URLEncoder.encode(partType, StandardCharsets.UTF_8);
        postData += "&Part Description=" + URLEncoder.encode(partDesc, StandardCharsets.UTF_8);
        postData += "&Part Note=" + URLEncoder.encode(partNote, StandardCharsets.UTF_8);
        postData += "&Part Number=" + URLEncoder.encode(partNumber, StandardCharsets.UTF_8);

        conn.setDoOutput(true);
        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
        wr.write(postData);
        wr.flush();

        int responseCode = conn.getResponseCode();

        if (responseCode == 200){
            return "POST Succeeded";
        }
        else if (responseCode == 401){
            return "Not Authorized";
        }
        else{
            return "Response code: " + responseCode;
        }
    }

    public static String deleteUser(String token, String username) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) tableUrl.openConnection();
        conn.setRequestMethod("POST");

        byte[] encodedToken = Base64.getEncoder().encode(token.getBytes(StandardCharsets.UTF_8));
        String authHeaderValue = "Basic " + new String(encodedToken);

        conn.setRequestProperty("authorization", authHeaderValue);

        String postData = "Action=" + URLEncoder.encode("Delete User", StandardCharsets.UTF_8);
        postData += "&Delete Username=" + URLEncoder.encode(username, StandardCharsets.UTF_8);

        conn.setDoOutput(true);
        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
        wr.write(postData);
        wr.flush();

        int responseCode = conn.getResponseCode();

        if (responseCode == 200){
            return "POST Succeeded";
        }
        else if (responseCode == 401){
            return "Not Authorized";
        }
        else{
            return "Response code: " + responseCode;
        }
    }

    public static String deleteFromTable(String token, String partNumber) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) tableUrl.openConnection();
        conn.setRequestMethod("POST");

        byte[] encodedToken = Base64.getEncoder().encode(token.getBytes(StandardCharsets.UTF_8));
        String authHeaderValue = "Basic " + new String(encodedToken);

        conn.setRequestProperty("authorization", authHeaderValue);

        String postData = "Action=" + URLEncoder.encode("delete", StandardCharsets.UTF_8);
        postData += "&Part Number=" + URLEncoder.encode(partNumber, StandardCharsets.UTF_8);

        conn.setDoOutput(true);
        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
        wr.write(postData);
        wr.flush();

        int responseCode = conn.getResponseCode();

        if (responseCode == 200){
            return "POST Succeeded";
        }
        else if (responseCode == 401){
            return "Not Authorized";
        }
        else{
            return "Response code: " + responseCode;
        }
    }

    private static HashMap<Integer, HashMap<String,String>> stringToHashMap(String input){
        if (input.charAt(0) == '{'){
            input = input.substring(1, input.length() - 1);
        }

        String[] keyValuePairs = input.split("},");
        HashMap<Integer, HashMap<String, String>> hash = new HashMap<>();

        for (String pair: keyValuePairs){
            HashMap<String, String> h = new HashMap<>();
            String[] entry = pair.split("=\\{");

            String[] keyPairs = entry[1].split(",");
            for (String p: keyPairs){
                String[] f = p.split("=");
                if (f[1].contains("}")){
                    f[1] = f[1].substring(0, f[1].length() - 1);
                }
                h.put(f[0].strip(), f[1].strip());
            }
            hash.put(Integer.parseInt(entry[0].strip()), h);
        }
        return hash;
    }
}
