package com.cloudbees.jenkins;

import org.htmlunit.html.HtmlForm;
import org.htmlunit.html.HtmlPage;
import org.jenkinsci.plugins.github.GitHubPlugin;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URL;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Test Class for {@link GitHubPushTrigger}.
 *
 * @author Seiji Sogabe
 */
//@Ignore("Have troubles with memory consumption")
public class GlobalConfigSubmitTest {

    public static final String HOOK_URL_INPUT = "hookUrl";

    private static final String WEBHOOK_URL = "http://jenkinsci.example.com/jenkins/github-webhook/";

    @Rule
    public JenkinsRule jenkins = new JenkinsRule();

    @Test
    public void shouldSetHookUrl() throws Exception {
        HtmlForm form = globalConfig();

        form.getInputByName(HOOK_URL_INPUT).setValue(WEBHOOK_URL);
        jenkins.submit(form);

        assertThat(GitHubPlugin.configuration().getHookUrlObject(), equalTo(new URL(WEBHOOK_URL)));
        assertThat(GitHubPlugin.configuration().getHookUrl(), equalTo(WEBHOOK_URL));
    }

    @Test
    public void shouldResetHookUrlIfEmpty() throws Exception {
        GitHubPlugin.configuration().setHookUrl(WEBHOOK_URL);

        HtmlForm form = globalConfig();

        form.getInputByName(HOOK_URL_INPUT).setValue("");
        jenkins.submit(form);

        assertThat(GitHubPlugin.configuration().getHookUrlObject().toString(), equalTo(new URL(WEBHOOK_URL)));
        assertThat(GitHubPlugin.configuration().getHookUrl(), equalTo(null));
    }

    public HtmlForm globalConfig() throws IOException, SAXException {
        JenkinsRule.WebClient client = configureWebClient();
        HtmlPage p = client.goTo("configure");
        return p.getFormByName("config");
    }

    private JenkinsRule.WebClient configureWebClient() {
        JenkinsRule.WebClient client = jenkins.createWebClient();
        client.setJavaScriptEnabled(true);
        return client;
    }
}
