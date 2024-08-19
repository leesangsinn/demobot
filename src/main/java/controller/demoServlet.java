package controller;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

@WebServlet(name = "demoServlet", value = "/demoServlet")
public class demoServlet extends HttpServlet {

    private final String botToken = "7183647995:AAHugg4mwrOo6XE7bbdMtl9gCT3oDS1cHOg"; // Thay YOUR_BOT_TOKEN bằng token bot của bạn

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Đọc JSON từ request
        BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();

        // Phân tích JSON
        JSONObject json = new JSONObject(sb.toString());
        JSONObject message = json.getJSONObject("message");
        String chatId = message.getJSONObject("chat").getString("id");
        String text = message.getString("text");

        // Gửi phản hồi về Telegram
        String urlString = "https://api.telegram.org/bot" + botToken + "/sendMessage?chat_id=" + chatId + "&text=" + text;
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.getResponseCode(); // Gọi để gửi yêu cầu

        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        out.println("Webhook processed successfully.");
    }
}