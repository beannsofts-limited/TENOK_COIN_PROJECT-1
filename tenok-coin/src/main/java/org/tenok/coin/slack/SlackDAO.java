package org.tenok.coin.slack;

import java.net.URI;

import com.slack.api.Slack;
import com.slack.api.util.http.SlackHttpClient;

import org.tenok.coin.type.CoinEnum;
import org.tenok.coin.type.OrderTypeEnum;
import org.tenok.coin.type.SideEnum;

public class SlackDAO {
    private static String botToken = null;
    private static String channel = null;
    private Slack slackInstance = null;

    private SlackDAO() {
        slackInstance = Slack.getInstance();
    }

    public static void setBotToken(String botToken) {
        SlackDAO.botToken = botToken;
    }

    public static void setChannel(String channel) {
        SlackDAO.channel = channel;
    }

    public void sendBuyMessage(CoinEnum coinType, SideEnum side, OrderTypeEnum orderType) {
        // send(String.format("%s을 %s개 %s하였습니다.", coinType.getLiteral(), orderType.name(), side.getKorean());
    }

    public void sendException(Throwable t) {
      
       
        // send(String.format("Exception 발생 %s", t.getMessage());
    }

    public void sendText(String text) {

    }

    public void sendTodayProfit() {

    }

    private static class SlackDAOHolder {
        public static final SlackDAO INSTANCE = new SlackDAO();
    }

    public static SlackDAO getInstance() throws NoSuchFieldException {
        if (botToken == null || channel == null) {
            throw new NoSuchFieldException("botToken, channel이 set되어 있지 않음.");
        }
        return SlackDAOHolder.INSTANCE;
    }

}
