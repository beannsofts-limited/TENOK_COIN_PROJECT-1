package org.tenok.coin.slack;

import java.io.IOException;

import com.slack.api.Slack;
import com.slack.api.webhook.WebhookResponse;

import org.tenok.coin.data.impl.AuthDecryptor;
import org.tenok.coin.type.CoinEnum;
import org.tenok.coin.type.SideEnum;
import org.tenok.coin.type.TIFEnum;

public class SlackSender {
    private String webhookUrl;
    private Slack slackInstance;
    WebhookResponse response;

    private SlackSender() {
        slackInstance = Slack.getInstance();
        this.webhookUrl = AuthDecryptor.getInstance().getSlackWebhookURL();
    }

    public WebhookResponse sendTradingMessage(CoinEnum coinType, SideEnum side, double qty, int leverage, TIFEnum tif) {
        try {
            String message = String.format("%s %.3f개 %d레버리지로 %s 주문 완료. 주문타입: %s", coinType.getKorean(), qty, leverage,
                    side.getKorean(), tif.getApiString());
            String payload = String.format("{\"text\":\"%s\"}", message);
            response = slackInstance.send(webhookUrl, payload);
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Trading Message sending 실패");

    }

    public WebhookResponse sendException(Throwable t) {
        try {
            String payload = String.format("{\"text\":\"Exception 발생%n%n%s\"}", t.toString());
            response = slackInstance.send(webhookUrl, payload);
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
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Text Sending 실패");
    }

    private static class SlackDAOHolder {
        public static final SlackSender INSTANCE = new SlackSender();
    }

    public static SlackSender getInstance() {
        if (SlackDAOHolder.INSTANCE.webhookUrl == null) {
            throw new RuntimeException("webhookUrl이 set되어 있지 않음.");
        }
        return SlackDAOHolder.INSTANCE;
    }
}
