package com.blackbetty;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;

public class Main {

    final static private String token = "NjM2MjEzNTA3NzcxMzM0NzAx.Xa8WBg.ngk5-DSti8EMGPb7bFZUGPT58x0";

    public static void main(String[] args) {

        try {
            new JDABuilder(token)
                    .addEventListeners(new AudioEcho())
                    .setActivity(Activity.listening("grane jest tutaj"))
                    .setStatus(OnlineStatus.DO_NOT_DISTURB)
                    .build();
        } catch (LoginException e) {
            System.out.println("Unable to login bot");
            e.printStackTrace();
        } finally {
            System.out.println("Login success");
        }

    }
}
