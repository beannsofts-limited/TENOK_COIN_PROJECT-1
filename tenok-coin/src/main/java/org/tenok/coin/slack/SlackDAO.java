package org.tenok.coin.slack;

import java.io.IOException;

import com.slack.api.Slack;
import com.slack.api.webhook.WebhookResponse;

import org.apache.log4j.Logger;
import org.tenok.coin.data.impl.AuthDecryptor;
import org.tenok.coin.type.CoinEnum;
import org.tenok.coin.type.SideEnum;

public class SlackDAO {
    private String webhookUrl;
    private Slack slackInstance;
    private static Logger logger = Logger.getLogger(SlackDAO.class);

    WebhookResponse response;

    private SlackDAO() {
        slackInstance = Slack.getInstance();
        this.webhookUrl = AuthDecryptor.getInstance().getSlackWebhookURL();
    }

    public WebhookResponse sendTradingMessage(CoinEnum coinType, SideEnum side, double qty) {
        // send(String.format("%s을 %s개 %s하였습니다.", coinType.getLiteral(),
        try {

            String payload = String.format("{\"text\":\"%s %f개 %s\"}", coinType.getKorean(), qty, side.getKorean());
            response = slackInstance.send(webhookUrl, payload);
            logger.debug(response);
            return response;
        } catch (IOException e) {

            e.printStackTrace();
        }
        throw new RuntimeException("Trading Message sending 실패");

    }

    public WebhookResponse sendException(Throwable t) {

        try {
            String payload = String.format("{\"text\":\"Exception 발생%n%n%s\"}", t.getMessage());
            response = slackInstance.send(webhookUrl, payload);
            logger.debug(response);
            return response;
        } catch (IOException e) {

            e.printStackTrace();
        }
        throw new RuntimeException("Exception Message sending 실패");
    }

    public WebhookResponse sendText(String text) {
        try {
            String payload = String.format("{\"text\":\"%s\"}", text);
            response = slackInstance.send(webhookUrl, payload);
            logger.debug(response);
            return response;
        } catch (IOException e) {

            e.printStackTrace();
        }
        throw new RuntimeException("Text Sending 실패");
    }

    private static class SlackDAOHolder {
        public static final SlackDAO INSTANCE = new SlackDAO();
    }

    public static SlackDAO getInstance() {
        if (SlackDAOHolder.INSTANCE.webhookUrl == null) {
            throw new RuntimeException("webhookUrl이 set되어 있지 않음.");
        }
        return SlackDAOHolder.INSTANCE;
    }

}
