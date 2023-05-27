package com.kongpf8848.dmail.login;

import android.content.Intent;
import android.os.Bundle;

import com.kongpf8848.dmail.R;
import com.kongpf8848.dmail.activity.MessageViewListActivity;
import com.kongpf8848.dmail.bean.MailConfig;
import com.kongpf8848.dmail.bean.OAuthToken;
import com.kongpf8848.dmail.login.oauth.DaggerOAuthComponent;
import com.kongpf8848.dmail.login.oauth.OAuthConfiguration;
import com.kongpf8848.dmail.login.oauth.qualifiers.Google;
import com.kongpf8848.dmail.login.oauth.qualifiers.Hotmail;
import com.kongpf8848.dmail.login.oauth.qualifiers.Yahoo;
import com.kongpf8848.dmail.mailcore.MailCore2Api;
import com.kongpf8848.dmail.mailcore.SessionManager;
import com.libmailcore.ConnectionType;

import javax.inject.Inject;

public class LoginActivity extends OAuthActivity {

    private MailConfig imapConfig;

    @Inject
    @Google
    OAuthConfiguration googleConfiguration;

    @Inject
    @Hotmail
    OAuthConfiguration hotmailConfiguration;

    @Inject
    @Yahoo
    OAuthConfiguration yahooConfiguration;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        DaggerOAuthComponent.builder().build().inject(this);

        findViewById(R.id.ll_gmail).setOnClickListener(v->onClickGmail());
        findViewById(R.id.ll_outlook).setOnClickListener(v->onClickOutlook());
        findViewById(R.id.ll_yahoo).setOnClickListener(v->onClickYahoo());
        findViewById(R.id.ll_exchange).setOnClickListener(v->onClickExchange());
        findViewById(R.id.ll_other).setOnClickListener(v->onClickOther());

    }

    @Override
    public void onOAuthTokenSuccess(String address, OAuthToken token) {
        MailCore2Api.Companion.getInstance().setImapSession(
                SessionManager.INSTANCE.buildImapSession(
                        address,
                        null,
                        imapConfig.host,
                imapConfig.port,
                imapConfig.connection_type,
                token
            )
        );
        jumpToMessageList();
    }

    private void onClickGmail() {
        imapConfig = new MailConfig();
        imapConfig.host = "imap.gmail.com";
        imapConfig.connection_type = ConnectionType.ConnectionTypeTLS;
        imapConfig.port = 993;
        doAuth(googleConfiguration, null);
    }

    private void onClickOutlook() {
        imapConfig = new MailConfig();
        imapConfig.host = "imap-mail.outlook.com";
        imapConfig.connection_type = ConnectionType.ConnectionTypeTLS;
        imapConfig.port = 993;
        doAuth(hotmailConfiguration, null);
    }

    private void onClickYahoo() {
        imapConfig = new MailConfig();
        imapConfig.host = "imap.mail.yahoo.com";
        imapConfig.connection_type = ConnectionType.ConnectionTypeTLS;
        imapConfig.port = 993;
        doAuth(yahooConfiguration, null);
    }

    private void onClickOther() {
        Intent intent = new Intent(this, MailAccountAuthActivity.class);
        intent.putExtra("accountType",DMAccountType.TYPE_IMAP.toString());
        startActivity(intent);
    }

    private void onClickExchange() {
        Intent intent = new Intent(this, MailAccountAuthActivity.class);
        intent.putExtra("accountType",DMAccountType.TYPE_EXCHAGE.toString());
        startActivity(intent);
    }

    private void jumpToMessageList() {
        Intent intent = new Intent(this, MessageViewListActivity.class);
        startActivity(intent);
    }
}
