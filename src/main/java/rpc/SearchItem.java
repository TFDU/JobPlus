package rpc;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

import db.MySQLConnection;
import entity.Item;
import org.json.JSONObject;
import org.json.JSONArray;



import external.GitHubClient;


@WebServlet(name = "SearchItem", urlPatterns = {"/search"})
public class SearchItem extends HttpServlet {


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.setStatus(403);
            return;
        }
        String userId = request.getParameter("user_id");
        double lat = Double.parseDouble(request.getParameter("lat"));
        double lon = Double.parseDouble(request.getParameter("lon"));

        GitHubClient client = new GitHubClient();
        List<Item> items = client.search(lat, lon, null);

        MySQLConnection connection = new MySQLConnection();
        Set<String> favoritedItemIds = connection.getFavoriteItemIds(userId);
        connection.close();
        JSONArray array = new JSONArray();
        for (Item item : items) {
            JSONObject obj = item.toJSONObject();
            obj.put("favorite", favoritedItemIds.contains(item.getItemId()));
            array.put(obj);
        }
        RpcHelper.writeJsonArray(response, array);
    }
}
