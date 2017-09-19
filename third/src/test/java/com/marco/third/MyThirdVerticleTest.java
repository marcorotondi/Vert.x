package com.marco.third;

import com.marco.third.model.PostBean;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.net.ServerSocket;


@RunWith(VertxUnitRunner.class)
public class MyThirdVerticleTest {
    private Vertx vertx;

    private int port = 9999;

    @Before
    public void init(TestContext context) throws IOException {
        vertx = Vertx.vertx();

        final ServerSocket socket = new ServerSocket(0);
        port = socket.getLocalPort();
        socket.close();

        DeploymentOptions options = new DeploymentOptions()
                .setConfig(new JsonObject().put("http.port", port));
        vertx.deployVerticle(MyThirdVerticle.class.getName(), options, context.asyncAssertSuccess());
    }

    @After
    public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void testMyApp(TestContext context) {
        final Async async = context.async();

        vertx.createHttpClient().getNow(port, "localhost", "/", response -> response.handler(body -> {
            context.assertTrue(body.toString().contains("Intre' Camp"));
            async.complete();
        }));
    }

    @Test
    public void checkIndexPage(TestContext context) {
        final Async async = context.async();

        vertx.createHttpClient().getNow(port, "localhost", "/asset/index.html", response -> {
            context.assertEquals(response.statusCode(), 200);
            context.assertTrue(response.headers().get("content-type").contains("text/html"));

            response.bodyHandler(body -> {
                context.assertTrue(body.toString().contains("<title>Vert.x Demo page</title>"));
                async.complete();
            });

        });
    }

    @Test
    public void checkAddPosts(TestContext context) {
        Async async = context.async();
        final String json = Json.encodePrettily(new PostBean("Test Post", "This is a post from test"));
        final String length = Integer.toString(json.length());
        vertx.createHttpClient().post(port, "localhost", "/api/posts")
                .putHeader("content-type", "application/json")
                .putHeader("content-length", length)
                .handler(response -> {
                    context.assertEquals(response.statusCode(), 201);
                    context.assertTrue(response.headers().get("content-type").contains("application/json"));
                    response.bodyHandler(body -> {
                        final PostBean post = Json.decodeValue(body.toString(), PostBean.class);
                        context.assertEquals(post.getTitle(), "Test Post");
                        context.assertEquals(post.getMessage(), "This is a post from test");
                        context.assertNotNull(post.getId());
                        async.complete();
                    });
                })
                .write(json)
                .end();
    }
}
