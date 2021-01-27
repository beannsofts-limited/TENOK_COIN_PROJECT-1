package org.tenok.coin.slack;

import java.io.IOException;

import com.slack.api.Slack;
import com.slack.api.webhook.WebhookResponse;

import org.tenok.coin.type.CoinEnum;
import org.tenok.coin.type.SideEnum;

public class SlackDAO {
    private static String botToken="0";
    private static String webhookUrl;
    private Slack slackInstance;

    WebhookResponse response;

    private SlackDAO() {
        slackInstance = Slack.getInstance();
    }

    public static void setBotToken(String botToken) {
        SlackDAO.botToken = botToken;
    }

    public static void setWebhookUrl(String webhookUrl) {
        SlackDAO.webhookUrl = webhookUrl;
    }

    public WebhookResponse sendTradingMessage(CoinEnum coinType, SideEnum side, double qty) {
        // send(String.format("%s을 %s개 %s하였습니다.", coinType.getLiteral(),
        // orderType.name(), side.getKorean());
        try {
            
            String payload = String.format("{\"text\":\"%s %f개 %s\"}", coinType.getKorean(), qty, side.getKorean());
            response = slackInstance.send(webhookUrl, payload);
            System.out.println(response);
            return response;
        } catch (IOException e) {

            e.printStackTrace();
        }
        throw new RuntimeException("Trading Message sending 실패");

    }

    public void sendException(Throwable t) {

        try {
            String payload = String.format("{\"text\":\"Exception 발생\n%s\"}", t.getMessage());
            response = slackInstance.send(webhookUrl, payload);
            System.out.println(response);
           
        } catch (IOException e) {

            e.printStackTrace();
        }
        throw new RuntimeException("Exception Message sending 실패");
        // send(String.format("Exception 발생 %s", t.getMessage());
    }

    private static class SlackDAOHolder {
        public static final SlackDAO INSTANCE = new SlackDAO();
    }

    public static SlackDAO getInstance() throws NoSuchFieldException {
        if ( botToken ==null || webhookUrl == null) {
            throw new NoSuchFieldException(" webhookUrl이 set되어 있지 않음.");
        }
        return SlackDAOHolder.INSTANCE;
    }

}
