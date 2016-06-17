package com.example.maven;

import com.example.javaee.todos.Todo;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlUnorderedList;
import io.restassured.http.ContentType;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

@RunWith(Arquillian.class)
public class ViewTodosTest {

    final List<Todo> todoList = Arrays.asList(Todo.aTodo().withTitle("first").build(),
            Todo.aTodo().withTitle("second").build(),
            Todo.aTodo().withTitle("third").isCompleted(true).build());
    @ArquillianResource
    URL baseUrl;

    @Deployment
    public static WebArchive createDeployment() {
        File war = Maven.resolver().loadPomFromFile("pom.xml")
                .resolve("com.example.maven:assemble:war:1.0-SNAPSHOT").withTransitivity().asSingleFile();

        return ShrinkWrap.create(WebArchive.class, "ROOT.war")
                .as(ZipImporter.class)
                .importFrom(war).as(WebArchive.class);
    }

    @Test
    @RunAsClient
    @InSequence(1)
    public void populate_store_with_data() {
        todoList.forEach(t -> {
            given().contentType(ContentType.JSON)
                    .body(t).
                    when().post(appUrl(baseUrl, "/todos"));
        });
    }

    @Test
    @RunAsClient
    @InSequence(2)
    public void should_return_three_todos() throws IOException {
        Todo[] todos = get(appUrl(baseUrl, "/todos")).as(Todo[].class);

        Arrays.asList(todos).forEach(t ->
                assertThat(t.getId(), is(notNullValue()))
        );

        assertThat(todos[0].getTitle(), equalTo(todoList.get(0).getTitle()));
        assertThat(todos[2].getTitle(), equalTo(todoList.get(2).getTitle()));
        assertThat(todos[2].isCompleted(), equalTo(todoList.get(2).isCompleted()));
    }

    @Test
    @RunAsClient
    @InSequence(3)
    public void should_render_three_todos() throws IOException, InterruptedException {
        final WebClient webClient = new WebClient(BrowserVersion.FIREFOX_45);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setCssEnabled(false);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());

        final HtmlPage page = webClient.getPage(appUrl(baseUrl, "/"));
        final HtmlUnorderedList ul = page.getHtmlElementById("todo-list");
        assertThat(ul, is(notNullValue()));
    }

    private URL appUrl(URL baseUrl, String path) {
        int port = baseUrl.getPort();
        String host = System.getProperty("wildflyHost");

        try {
            return new URL("http", host, port, path);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
